# Configuración uniforme para todos los ambientes
# Elimina diferencias innecesarias entre dev/qa/pdn

# Data sources para referenciar infraestructura transversal
data "aws_dynamodb_table" "franquicias" {
  name = "business-franquicias-${var.env}"
}

data "aws_dynamodb_table" "sucursales" {
  name = "business-sucursales-${var.env}"
}

data "aws_dynamodb_table" "productos" {
  name = "business-productos-${var.env}"
}

data "aws_vpc" "main" {
  default = true
}

data "aws_subnets" "public" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.main.id]
  }
  filter {
    name   = "map-public-ip-on-launch"
    values = ["true"]
  }
}

data "aws_region" "current" {}

# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = local.cluster_name

  configuration {
    execute_command_configuration {
      logging = "OVERRIDE"
      log_configuration {
        cloud_watch_log_group_name = aws_cloudwatch_log_group.ecs_cluster.name
      }
    }
  }

  tags = local.common_tags
}

# ECS Service - configuración uniforme para todos los ambientes
resource "aws_ecs_service" "app" {
  name            = local.service_name
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = local.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.public.ids
    security_groups  = [aws_security_group.ecs_service.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.app.arn
    container_name   = local.container_name
    container_port   = 8080
  }

  depends_on = [aws_lb_listener.app]

  tags = local.common_tags
}

# ECS Task Definition - uniforme para todos los ambientes
resource "aws_ecs_task_definition" "app" {
  family                   = local.task_family
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = local.cpu
  memory                   = local.memory
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn
  task_role_arn           = aws_iam_role.ecs_task.arn

  container_definitions = jsonencode([
    {
      name  = local.container_name
      image = local.container_image
      
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]

      environment = [
        {
          name  = "SPRING_PROFILES_ACTIVE"
          value = var.env
        },
        {
          name  = "AWS_REGION"
          value = data.aws_region.current.name
        },
        {
          name  = "ENVIRONMENT"
          value = var.env
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.app.name
          awslogs-region        = data.aws_region.current.name
          awslogs-stream-prefix = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1"]
        interval    = 60
        timeout     = 10
        retries     = 5
        startPeriod = 120
      }
    }
  ])

  tags = local.common_tags
}

# Application Load Balancer - uniforme
resource "aws_lb" "app" {
  name               = local.alb_name
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = data.aws_subnets.public.ids

  enable_deletion_protection = local.enable_deletion_protection

  tags = local.common_tags
}

resource "aws_lb_target_group" "app" {
  name        = local.target_group_name
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = data.aws_vpc.main.id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    interval            = 60
    matcher             = "200"
    path                = "/actuator/health"
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 10
    unhealthy_threshold = 3
  }

  tags = local.common_tags
}

resource "aws_lb_listener" "app" {
  load_balancer_arn = aws_lb.app.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }

  tags_all = local.common_tags
}

# Security Groups - uniformes
resource "aws_security_group" "alb" {
  name_prefix = "${local.service_name}-alb-"
  vpc_id      = data.aws_vpc.main.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle {
    create_before_destroy = true
  }

  tags = merge(local.common_tags, {
    Name = "${local.service_name}-alb"
  })
}

resource "aws_security_group" "ecs_service" {
  name_prefix = "${local.service_name}-ecs-"
  vpc_id      = data.aws_vpc.main.id

  ingress {
    description     = "HTTP from ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle {
    create_before_destroy = true
  }

  tags = merge(local.common_tags, {
    Name = "${local.service_name}-ecs"
  })
}

# Data source para encontrar el security group del VPC endpoint existente
data "aws_security_group" "vpc_endpoint" {
  filter {
    name   = "tag:Name"
    values = ["${local.service_name}-vpc-endpoint-sg"]
  }
  vpc_id = data.aws_vpc.main.id
}

# Security Group rule para VPC endpoint de CloudWatch Logs
resource "aws_security_group_rule" "vpc_endpoint_from_ecs" {
  type                     = "ingress"
  from_port                = 443
  to_port                  = 443
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.ecs_service.id
  security_group_id        = data.aws_security_group.vpc_endpoint.id
}

# CloudWatch Log Groups - uniformes
resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/${local.service_name}"
  retention_in_days = local.log_retention_days

  tags = local.common_tags
}

resource "aws_cloudwatch_log_group" "ecs_cluster" {
  name              = "/aws/ecs/cluster/${local.cluster_name}"
  retention_in_days = local.log_retention_days

  tags = local.common_tags
}

# IAM Roles - uniformes
resource "aws_iam_role" "ecs_task_execution" {
  name = "${local.service_name}-ecs-task-execution"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_task" {
  name = "${local.service_name}-ecs-task"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = local.common_tags
}

# IAM Policy para acceso a DynamoDB - uniforme
resource "aws_iam_role_policy" "ecs_task_dynamodb" {
  name = "${local.service_name}-dynamodb"
  role = aws_iam_role.ecs_task.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:Query",
          "dynamodb:Scan",
          "dynamodb:BatchGetItem",
          "dynamodb:BatchWriteItem"
        ]
        Resource = [
          data.aws_dynamodb_table.franquicias.arn,
          data.aws_dynamodb_table.sucursales.arn,
          data.aws_dynamodb_table.productos.arn,
          "${data.aws_dynamodb_table.franquicias.arn}/index/*",
          "${data.aws_dynamodb_table.sucursales.arn}/index/*",
          "${data.aws_dynamodb_table.productos.arn}/index/*"
        ]
      }
    ]
  })
}
