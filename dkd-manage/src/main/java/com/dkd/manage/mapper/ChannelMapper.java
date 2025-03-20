package com.dkd.manage.mapper;

import java.util.List;
import com.dkd.manage.domain.Channel;
import com.dkd.manage.domain.vo.ChannelVo;
import org.apache.ibatis.annotations.Param;

/**
 * 售货机货道Mapper接口
 * 
 * @author itheima
 * @date 2025-03-10
 */
public interface ChannelMapper 
{
    /**
     * 查询售货机货道
     * 
     * @param id 售货机货道主键
     * @return 售货机货道
     */
    public Channel selectChannelById(Long id);

    /**
     * 查询售货机货道列表
     * 
     * @param channel 售货机货道
     * @return 售货机货道集合
     */
    public List<Channel> selectChannelList(Channel channel);

    /**
     * 新增售货机货道
     * 
     * @param channel 售货机货道
     * @return 结果
     */
    public int insertChannel(Channel channel);

    /**
     * 修改售货机货道
     * 
     * @param channel 售货机货道
     * @return 结果
     */
    public int updateChannel(Channel channel);

    /**
     * 删除售货机货道
     * 
     * @param id 售货机货道主键
     * @return 结果
     */
    public int deleteChannelById(Long id);

    /**
     * 批量删除售货机货道
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteChannelByIds(Long[] ids);

    void batchInsertChannel(List<Channel> channelList);

    int countChannelBySkuIds(Long[] skuIds);

    /**
     * 根据售货机编号查询货道列表
     * @param innerCode
     * @return ChannelVo集合
     */
    List<ChannelVo> selectChannelVoListByInnerCode(String innerCode);

    /**
     * 根据售货机编号和货道编号查询货道信息
     * @param innerCode
     * @return Channel集合
     */
    Channel getChannelInfo(@Param("innerCode")String innerCode, @Param("channelCode")String channelCode);

    /**
     * 批量修改货道
     * @param list
     * @return 结果
     */
    int batchUpdateChannel(List<Channel> list);
}
