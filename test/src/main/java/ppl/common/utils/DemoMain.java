package ppl.common.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ppl.common.utils.json.jackson.JsonUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class DemoMain {

    public static class A {
        @JsonFormat(pattern = "yyyy-MMMM-dd HH:mm:ss", timezone = "Europe/Paris", locale = "zh")
        private LocalDateTime date;

        public LocalDateTime getDate() {
            return date;
        }

        public void setDate(LocalDateTime date) {
            this.date = date;
        }
    }
    public static void main(String[] args) throws IOException {
        A a = new A();
        a.setDate(LocalDateTime.now());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println(objectMapper.writeValueAsString(a));
        System.out.println(JsonUtils.write(a));

        A b = objectMapper.readValue("{\"date\":\"2024-十月-13 11:04:23\"}", A.class);
        A b1 = JsonUtils.read("{\"date\":\"2024-十月-13 11:04:23\"}", A.class);
        System.out.println(b.getDate());
        System.out.println(b1.getDate());
    }
}
