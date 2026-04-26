package com.factory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.factory.entity.Material;

public interface MaterialService extends IService<Material> {
    Long add(Material material);
    int updateStock(Long id, String type, int quantity);
}