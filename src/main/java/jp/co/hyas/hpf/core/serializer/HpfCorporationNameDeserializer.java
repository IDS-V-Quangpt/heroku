package jp.co.hyas.hpf.core.serializer;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import jp.co.hyas.hpf.core.HpfConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class HpfCorporationNameDeserializer extends JsonDeserializer<String> {
	@Override
    public String deserialize(@NotNull JsonParser p, DeserializationContext ctxt) throws IOException {
    	//return p.getCodec().readTree(p).toString();
        //return p.getCodec().readTree(p).toString();
        return HpfConvert.CorporationName(p.getText());
	}
}
