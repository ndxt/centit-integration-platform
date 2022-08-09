package com.centit.framework.tenant.po;

import com.centit.support.database.orm.GeneratorType;
import com.centit.support.database.orm.ValueGenerator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "移动端版本管理信息", description = "移动端版本管理信息")
@Entity
@Data
@Table(name = "F_APP_INFO")
public class AppInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @ValueGenerator(strategy = GeneratorType.UUID22)
    @Length(max = 32)
    @ApiModelProperty(value = "主键id", name = "id")
    private String id;

    @Column(name = "VERSION_ID")
    @Length(max = 32)
    @ApiModelProperty(value = "版本号", name = "versionId")
    private String versionId;

    @Column(name = "VERSION_DES")
    @Length(max = 500)
    @ApiModelProperty(value = "版本描述", name = "versionDes")
    private String versionDes;

    @Column(name = "APP_TYPE")
    @Length(max = 32)
    @ApiModelProperty(value = "设备类型", name = "appType")
    private String appType;

    @Column(name = "FILE_ID")
    @Length(max = 100)
    @ApiModelProperty(value = "文件Id", name = "fileId")
    private String fileId;

    @Column(name = "FILE_NAME")
    @Length(max = 100)
    @ApiModelProperty(value = "文件名称", name = "fileName")
    private String fileName;

    @Column(name = "FILE_URL")
    @Length(max = 500)
    @ApiModelProperty(value = "文件下载地址", name = "fileUrl")
    private String fileUrl;

    @Column(name = "CREATOR")
    @Length(max = 32)
    @ApiModelProperty(value = "申请人", name = "creator")
    private String creator;

    @Column(name = "CREATE_TIME")
    @ApiModelProperty(value = "创建时间", name = "createTime")
    @ValueGenerator(strategy = GeneratorType.FUNCTION, value = "today()")
    private Date createTime;

}
