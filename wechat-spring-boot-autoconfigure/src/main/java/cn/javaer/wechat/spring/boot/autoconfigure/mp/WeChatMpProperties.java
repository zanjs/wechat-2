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

package cn.javaer.wechat.spring.boot.autoconfigure.mp;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangpeng
 */
@SuppressWarnings("WeakerAccess")
@Setter
@Getter
@ConfigurationProperties(prefix = "wechat.mp")
public class WeChatMpProperties
{
    /** 公众号的唯一标识 */
    @NonNull private String appId;
    
    @NonNull private String appSecret;
    
    @NonNull private String notifyAddress;
    
    /** 授权后重定向的回调链接path */
    private String authorizeCodePath;
    
    private String generateAuthorizeUrlPath;
    
    /** 授权成功后的重定向地址 */
    @NonNull private String redirectView;
}
