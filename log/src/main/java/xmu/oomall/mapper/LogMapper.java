package xmu.oomall.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import xmu.oomall.domain.MallLog;

import java.util.List;

/**
 * @author liznsalt
 */
@Component
@Mapper
public interface LogMapper extends tk.mybatis.mapper.common.Mapper<MallLog> {
    /**
     * 添加日志
     * @param log 日志信息
     * @return 行数
     */
    int addLog(MallLog log);

    /**
     * 得到所有日志
     * @return 日志列表
     */
    List<MallLog> getAllLogs();

    /**
     * 检索日志
     * @param id 日志ID
     * @return 日志信息
     */
    MallLog findLogById(Integer id);

//    /**
//     * 根据Example条件进行查询
//     * @param example 条件
//     * @return 日志列表
//     */
//    List<MallLog> findLogsByExample(Example example);
}
