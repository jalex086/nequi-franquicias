package co.com.bancolombia.dynamodb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws.dynamodb")
public class DynamoDBProperties {
    private String region;
    private Tables tables = new Tables();
    
    @Data
    public static class Tables {
        private String franchises;
        private String branches;
        private String products;
    }
}
