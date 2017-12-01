package com.linkedin.samza.tools.avro;

import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.commons.lang.NotImplementedException;
import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;


public class AvroSerDeFactory implements SerdeFactory {

  public static String CFG_AVRO_SCHEMA = "serializers.avro.schema";

  @Override
  public Serde getSerde(String name, Config config) {
    return new AvroSerDe(config);
  }

  private class AvroSerDe implements Serde {
    private final Schema schema;

    public AvroSerDe(Config config) {
      schema = Schema.parse(config.get(CFG_AVRO_SCHEMA));
    }

    @Override
    public Object fromBytes(byte[] bytes) {
      GenericRecord record = null;
      try {
        record = genericRecordFromBytes(bytes, schema);
      } catch (IOException e) {
        throw new SamzaException("Unable to deserialize the record", e);
      }
      return record;
    }

    @Override
    public byte[] toBytes(Object o) {
      throw new NotImplementedException();
    }
  }

  private static <T> T genericRecordFromBytes(byte[] bytes, Schema schema) throws IOException {
    BinaryDecoder binDecoder = DecoderFactory.defaultFactory().createBinaryDecoder(bytes, null);
    GenericDatumReader<T> reader = new GenericDatumReader<>(schema);
    return reader.read(null, binDecoder);
  }
}
