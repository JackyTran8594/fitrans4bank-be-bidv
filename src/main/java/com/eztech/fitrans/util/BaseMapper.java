package com.eztech.fitrans.util;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SuppressWarnings({"java:S1117", "java:S119"})
public class BaseMapper<Model, DTO> {

  private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  private MapperFacade mapper;
  private Class<Model> model;
  private Class<DTO> dto;

  @SuppressWarnings("unchecked")
  public BaseMapper(Class<Model> model, Class<DTO> dto) {
    mapperFactory.classMap(model, dto)
        .constructorA()
        .constructorB()
        .byDefault()
        .register();
    mapper = mapperFactory.getMapperFacade();
    this.dto = dto;
    this.model = model;
  }

  public BaseMapper() {
    mapper = mapperFactory.getMapperFacade();
  }

  public DTO toDtoBean(Model model) {
    if(model == null){
      return null;
    }
    return mapper.map(model, dto);
  }

  public Model toPersistenceBean(DTO dtoBean) {
    return mapper.map(dtoBean, model);
  }

  public List<DTO> toDtoBean(Iterable<Model> models) {
    List<DTO> dtoBeans = new ArrayList<>();
    if (models == null) {
      return dtoBeans;
    }
    for (Model model : models) {
      dtoBeans.add(toDtoBean(model));
    }
    return dtoBeans;
  }

  public List<DTO> toDtoBean(List<Model> models) {
    List<DTO> dtoBeans = new ArrayList<>();

    if (models == null) {
      return dtoBeans;
    }
    for (Model model : models) {
      dtoBeans.add(toDtoBean(model));
    }
    return dtoBeans;
  }

  public List<Model> toPersistenceBean(List<DTO> dtoBeans) {
    List<Model> models = new ArrayList<>();
    if (dtoBeans == null || dtoBeans.isEmpty()) {
      return models;
    }
    for (DTO dtoBean : dtoBeans) {
      models.add(toPersistenceBean(dtoBean));
    }
    return models;
  }
}
