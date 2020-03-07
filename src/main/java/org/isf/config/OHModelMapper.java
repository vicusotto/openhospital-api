package org.isf.config;

import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OHModelMapper {

    @Bean
    public ModelMapper getModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new BlobToByteArrayConverter());
        modelMapper.addConverter(new ByteArrayToBlobConverter());
        return modelMapper;
    }
}

