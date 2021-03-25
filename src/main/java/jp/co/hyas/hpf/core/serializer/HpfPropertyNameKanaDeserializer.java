package jp.co.hyas.hpf.core.serializer;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import jp.co.hyas.hpf.core.HpfConvert;

public class HpfPropertyNameKanaDeserializer extends JsonDeserializer<String> {
	@Override
    public String deserialize(@NotNull JsonParser p, DeserializationContext ctxt) throws IOException {
        return HpfConvert.PropertyNameKana(p.getText());
	}
}
