package com.example.demo.mapper;

import com.example.demo.entity.CarbonAsset;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CarbonAssetMapper {

    // 根据 uid 查询该用户的资产
    @Select("SELECT * FROM carbon_asset WHERE uid = #{uid} ORDER BY id DESC")
    List<CarbonAsset> selectByUid(Integer uid);

    // 新增（必须带 uid）
    @Insert("INSERT INTO carbon_asset (uid, project_name, asset_type, amount) VALUES (#{uid}, #{projectName}, #{assetType}, #{amount})")
    int insert(CarbonAsset asset);

    // 修改
    @Update("UPDATE carbon_asset SET project_name=#{projectName}, asset_type=#{assetType}, amount=#{amount} WHERE id=#{id}")
    int update(CarbonAsset asset);

    // 删除
    @Delete("DELETE FROM carbon_asset WHERE id = #{id}")
    int deleteById(Integer id);
}