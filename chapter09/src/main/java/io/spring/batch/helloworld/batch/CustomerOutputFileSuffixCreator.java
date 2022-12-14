package io.spring.batch.helloworld.batch;

import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.stereotype.Component;

@Component
public class CustomerOutputFileSuffixCreator implements ResourceSuffixCreator {
    @Override
    public String getSuffix(int index) {
        return index + ".xml";
    }
}
