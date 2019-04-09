import com.google.common.collect.Iterables;
import grpc.LoadBalancerClient;
import org.jooby.Jooby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author jooby generator
 */
public class App extends Jooby {

  private List<String> productServiceIpList =  Arrays.asList("10.0.0.50", "10.0.0.51", "La 10.0.0.52");

  private final Iterator<String> productServiceRoundRobin = Iterables.cycle(productServiceIpList).iterator();

  private List<String> userServiceIpList =  Arrays.asList("10.0.0.53", "10.0.0.54", "La 10.0.0.55");

  private final Iterator<String> userServiceRoundRobin = Iterables.cycle(userServiceIpList).iterator();

  private final LoadBalancerClient loadBalancerClient = new LoadBalancerClient("localhost",3000);

  {
    get("/", () -> "Hello World!");

    get("/product", (req, rsp) -> productServiceRoundRobin.next());
  }

  public static void main(final String[] args) {
    run(App::new, args);
  }

}
