package com.avasiliuk.testtask.utils.mappers;


import com.avasiliuk.testtask.model.jooq.tables.records.ProductsRecord;
import com.avasiliuk.testtask.model.rest.NewProduct;
import com.avasiliuk.testtask.model.rest.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = GeneralMapper.class)
public abstract class ProductMapper {

    @Mapping(target = "id", ignore = true)
    public abstract ProductsRecord toProductsRecord(NewProduct newProduct);

    public abstract Product toProduct(ProductsRecord productsRecord);

    public abstract List<Product> toProductList(List<ProductsRecord> productsRecords);
}
