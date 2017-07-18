/*
 *    Copyright 2017 zhangpeng
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package cn.javaer.wechat.spring.boot.autoconfigure.pay;

import cn.javaer.wechat.sdk.pay.AbstractWeChatPayResponse;
import cn.javaer.wechat.sdk.pay.WeChatPayClient;
import cn.javaer.wechat.sdk.pay.WeChatPayException;
import cn.javaer.wechat.sdk.pay.WeChatPayNotifyResult;
import cn.javaer.wechat.sdk.pay.WeChatPayUnifiedOrderRequest;
import cn.javaer.wechat.sdk.pay.WeChatPayUnifiedOrderResponse;
import cn.javaer.wechat.sdk.util.SignUtil;
import io.vavr.control.Try;
import jodd.bean.BeanCopy;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author zhangpeng
 */
public class DefaultWeChatPayService implements WeChatPayService
{
    private final WeChatPayProperties weChatPayProperties;
    private final WeChatPayClient     weChatPayClient;
    
    public DefaultWeChatPayService(
        @NotNull final WeChatPayProperties weChatPayProperties,
        @NotNull final WeChatPayClient weChatPayClient)
    {
        this.weChatPayProperties = weChatPayProperties;
        this.weChatPayClient = weChatPayClient;
    }
    
    @Override
    public UnifiedOrderResponse unifiedOrder(@NotNull final UnifiedOrderRequest unifiedOrderRequest)
    {
        final WeChatPayUnifiedOrderRequest request = WeChatPayUnifiedOrderRequest.builder()
            .body(unifiedOrderRequest.getBody())
            .nonceStr(SignUtil.uuid())
            .outTradeNo(unifiedOrderRequest.getOutTradeNo())
            .productId(unifiedOrderRequest.getProductId())
            .totalFee(unifiedOrderRequest.getTotalFee())
            .appId(this.weChatPayProperties.getAppId())
            .mchId(this.weChatPayProperties.getMchId())
            .notifyUrl(this.weChatPayProperties.getNotifyUrl())
            .spbillCreateIp(this.weChatPayProperties.getClientIp())
            .tradeType("NATIVE")
            .build();
        request.setSign(SignUtil.sign(request, this.weChatPayProperties.getMchKey()));
        
        final Call<WeChatPayUnifiedOrderResponse> responseCall = this.weChatPayClient.unifiedOrder(request);
        final Response<WeChatPayUnifiedOrderResponse> response = Try.of(responseCall::execute).getOrElseThrow(WeChatPayException::new);
        checkResponse(response);
        final WeChatPayUnifiedOrderResponse successfulBody = response.body();
        checkResponseBody(successfulBody);
        
        final UnifiedOrderResponse unifiedOrderResponse = new UnifiedOrderResponse();
        unifiedOrderResponse.setCodeUrl(successfulBody.getCodeUrl());
        unifiedOrderResponse.setNonceStr(successfulBody.getNonceStr());
        unifiedOrderResponse.setPrepayId(successfulBody.getPrepayId());
        return unifiedOrderResponse;
    }
    
    @Override
    public NotifyResult notifyResult(@NotNull final WeChatPayNotifyResult apiNotifyResult)
    {
        checkResponseBody(apiNotifyResult);
        final NotifyResult notifyResult = new NotifyResult();
        BeanCopy.beans(apiNotifyResult, notifyResult).copy();
        return notifyResult;
    }
    
    private void checkResponse(final Response response)
    {
        if (!response.isSuccessful())
        {
            throw new WeChatPayException("Http response error, response:" + response.toString());
        }
    }
    
    private void checkResponseBody(final AbstractWeChatPayResponse response)
    {
        if (null == response)
        {
            throw new WeChatPayException("WeChat pay response is null");
        }
        if (!response.getSign().equals(SignUtil.sign(response, this.weChatPayProperties.getMchKey())))
        {
            throw new WeChatPayException("WeChat pay response 'sign' error");
        }
        if (!"SUCCESS".equals(response.getReturnCode()))
        {
            throw new WeChatPayException("WeChat pay response error, response:" + response.toString());
        }
        if (!"SUCCESS".equals(response.getResultCode()))
        {
            throw new WeChatPayException("WeChat pay response error, response:" + response.toString());
        }
    }
}