package org.isf.shared.mapper;

import java.util.List;

public interface OHMapper<FromType, ToType> {
    
    public ToType map2DTO(FromType fromObj);

    public FromType map2Model(ToType toObj);

    public List<ToType> map2DTOList(List<FromType> list);
}