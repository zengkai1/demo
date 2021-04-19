package com.example.demo.covertor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *      基础转换类
 * </p>
 *
 * @author: 曾凯
 * @Version: V1.0
 * @since:
 */
public abstract class BaseDOWrapper<E, V> {

    public BaseDOWrapper() {
    }

    /**
     * 对象转换
     * @param entity  要转换的实体类
     * @return 转换的结果实体类
     */
    public abstract V entityVO(E entity);

    public List<V> listVO(List<E> list) {
        return list.stream().map(this::entityVO).collect(Collectors.toList());
    }

    public IPage<V> pageVO(IPage<E> pages) {
        List<V> records = this.listVO(pages.getRecords());
        IPage<V> pageVo = new Page<>(pages.getCurrent(), pages.getSize(), pages.getTotal());
        pageVo.setRecords(records);
        return pageVo;
    }



}
