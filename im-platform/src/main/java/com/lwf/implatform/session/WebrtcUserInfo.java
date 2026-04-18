package com.lwf.implatform.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: LWF
 * @date: 2025-06-02
 * @version: 1.0
 */
@Data
@Schema(description = "用户信息")
public class WebrtcUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户头像")
    private String headImage;

    @Schema(description = "是否开启摄像头")
    private Boolean isCamera;

    @Schema(description = "是否开启麦克风")
    private Boolean isMicroPhone;
}
