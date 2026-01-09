package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.CarbonAsset;
import com.example.demo.mapper.CarbonAssetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/asset")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CarbonAssetController {

    @Autowired
    private CarbonAssetMapper carbonAssetMapper;

    // 1. 获取列表 (需要传 uid)
    @GetMapping("/list")
    public Result<List<CarbonAsset>> list(@RequestParam Integer uid) {
        return Result.success(carbonAssetMapper.selectByUid(uid));
    }

    // 2. 新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody CarbonAsset asset) {
        if (asset.getUid() == null) {
            return Result.error( "用户ID不能为空");
        }
        carbonAssetMapper.insert(asset);
        return Result.success();
    }

    // 3. 修改
    @PutMapping("/update")
    public Result<?> update(@RequestBody CarbonAsset asset) {
        carbonAssetMapper.update(asset);
        return Result.success();
    }

    // 4. 删除
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        carbonAssetMapper.deleteById(id);
        return Result.success();
    }
}