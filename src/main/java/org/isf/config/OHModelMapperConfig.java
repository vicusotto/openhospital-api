package org.isf.config;


import org.isf.shared.mapper.converter.BlobToByteArrayConverter;
import org.isf.shared.mapper.converter.ByteArrayToBlobConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OHModelMapperConfig {
    @Autowired
    protected BlobToByteArrayConverter blobToByteArrayConverter;

    @Autowired
    protected ByteArrayToBlobConverter byteArrayToBlobConverter;

    @Bean(name = "ohModelMapper")
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(blobToByteArrayConverter);
        modelMapper.addConverter(byteArrayToBlobConverter);
        return modelMapper;
    }
}
