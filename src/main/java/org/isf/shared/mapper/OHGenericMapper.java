package org.isf.shared.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class OHGenericMapper<SourceType, DestType> implements OHMapper<SourceType, DestType> {

    @Autowired
    @Qualifier("ohModelMapper")
    protected ModelMapper modelMapper;
    private Type sourceClass;
    private Type destClass;

    public OHGenericMapper(Class<SourceType> sourceClass, Class<DestType> destClass) {
        this.sourceClass = sourceClass;
        this.destClass = destClass;
    }

    @Override
    public DestType map2DTO(SourceType fromObj) {
        return modelMapper.map(fromObj, destClass);
    }

    @Override
    public SourceType map2Model(DestType toObj) {
        return modelMapper.map(toObj, sourceClass);
    }

    @Override
    public List<DestType> map2DTOList(List<SourceType> list) {
        return (List<DestType>) list.stream().map(it -> modelMapper.map(it, destClass)).collect(Collectors.toList());
    }
}