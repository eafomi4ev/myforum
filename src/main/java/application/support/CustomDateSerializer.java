//package application.support;
//
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import org.codehaus.jackson.JsonGenerator;
//import org.codehaus.jackson.JsonProcessingException;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Created by egor on 12.03.17.
// */
//public class CustomDateSerializer extends StdSerializer<Date> {
//
//    private SimpleDateFormat formatter
//            = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
//
//    public CustomDateSerializer() {
//        this(null);
//    }
//
//    public CustomDateSerializer(Class t) {
//        super(t);
//    }
//
//    @Override
//    public void serialize (Date value, JsonGenerator gen, SerializerProvider arg2)
//            throws IOException, JsonProcessingException {
//        gen.writeString(formatter.format(value));
//    }
//}