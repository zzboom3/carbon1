package com.example.demo.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    // === 配置信息 ===
    // 注意：在实际生产环境中，Key 应该放在配置文件里，不要硬编码在代码中
    private static final String API_KEY = "sk-DLXRrSaAZpEiqtkYr2Op6gs44R6CVu9j8fNXm0Zd4E0AZwCy";
    // OpenAI 标准接口地址后缀是 /chat/completions
    private static final String API_URL = "https://api.openai-proxy.org/v1/chat/completions";

    @PostMapping("/ask")
    public Result<String> ask(@RequestBody Map<String, String> params) {
        String userQuestion = params.get("question");

        // 1. 构建请求体 (符合 OpenAI 接口规范)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo"); // 使用 3.5 模型，速度快且便宜

        // 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();

        // [系统设定]：给 AI 设定人设，让它回答更专业
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个碳中和资产管理平台的智能顾问。请用专业、简洁、环保的口吻回答用户关于低碳、节能、碳交易等方面的问题。");
        messages.add(systemMessage);

        // [用户问题]
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userQuestion);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        try {
            // 2. 发送 HTTP POST 请求
            String jsonBody = JSONUtil.toJsonStr(requestBody);

            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY) // 认证头
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .timeout(20000) // 设置超时时间 20秒
                    .execute();

            // 3. 处理返回结果
            if (response.isOk()) {
                String resBody = response.body();
                // 解析复杂的 JSON 结构： choices -> [0] -> message -> content
                JSONObject jsonObject = JSONUtil.parseObj(resBody);
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String content = message.getStr("content");
                    return Result.success(content);
                }
            }

            return Result.error( "AI 响应异常: " + response.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error( "请求大模型失败：" + e.getMessage());
        }
    }
}