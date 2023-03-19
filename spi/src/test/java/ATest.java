;
import com.lxy.rpc.LxySpiApplication;
import com.lxy.rpc.config.ServiceDiscoverProperties;
import com.lxy.rpc.service.HelloService;
import com.lxy.rpc.spi.ExtensionLoader;
import com.lxy.rpc.spi.ExtensionLoaderFactory;
import com.lxy.rpc.spi.LogLXY;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = LxySpiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
public class ATest {
    @Autowired
    private ExtensionLoaderFactory extensionLoaderFactory;

    @Test
    public void  asdsa() throws InterruptedException {
        final ExtensionLoader<?> extensionLoaderByClass1 = extensionLoaderFactory.getExtensionLoaderByClass(LogLXY.class);
        final ExtensionLoader<LogLXY> extensionLoaderByClass = (ExtensionLoader<LogLXY>) extensionLoaderByClass1;
        final LogLXY lxy = extensionLoaderByClass.getExtension("aaa");
        lxy.hello();
    }
}
