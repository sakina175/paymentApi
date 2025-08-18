package Configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "payment.config")
public class PaymentConfiguration {
    private String apiKey;
    private String baseUrl;
    private int timeout;
    private String merchantId;
    private String currency;
    private String environment;
    
}
