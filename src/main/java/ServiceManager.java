import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

class ServiceManager {

    AddressWithPort getAddress(String url) {
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        ByteSequence key = ByteSequence.from(url.getBytes());
        ByteSequence wildcard = ByteSequence.from("\0".getBytes());
        KV kvClient = client.getKVClient();
        GetOption build = GetOption.newBuilder().withPrefix(key).build();
        try {
            List<KeyValue> kvs = kvClient.get(wildcard, build).get().getKvs();
            int random = new Random().nextInt(kvs.size());
            return new Gson().fromJson(kvs.get(random).getValue().toString(Charset.defaultCharset()), AddressWithPort.class);
        } catch (InterruptedException | ExecutionException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException("url not found");
        }
    }

}
