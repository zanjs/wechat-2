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

import cn.javaer.wechat.sdk.pay.WeChatPayClient;
import cn.javaer.wechat.sdk.pay.WeChatPayClientFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangpeng
 */
@Configuration
@ConditionalOnClass(name = "cn.javaer.wechat.spring.boot.starter.pay.ConditionalOnClassTrigger")
@EnableConfigurationProperties(WeChatPayProperties.class)
public class WeChatPayAutoConfiguration
{
    private final WeChatPayProperties weChatPayProperties;
    
    public WeChatPayAutoConfiguration(final WeChatPayProperties weChatPayProperties)
    {
        this.weChatPayProperties = weChatPayProperties;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public WeChatPayClient weChatPayClient()
    {
        return new WeChatPayClientFactory().create();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public WeChatPayService weChatPayService(final WeChatPayClient weChatPayClient)
    {
        return new DefaultWeChatPayService(this.weChatPayProperties, weChatPayClient);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public WeChatPayController weChatPayController(final ApplicationEventPublisher publisher)
    {
        return new WeChatPayController(publisher, this.weChatPayProperties);
    }
}
