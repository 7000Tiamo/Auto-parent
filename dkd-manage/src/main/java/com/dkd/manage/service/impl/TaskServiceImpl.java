package com.dkd.manage.service.impl;


import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dkd.common.constant.DkdContants;
import com.dkd.common.exception.ServiceException;
import com.dkd.common.utils.DateUtils;
import com.dkd.common.utils.StringUtils;
import com.dkd.manage.domain.Emp;
import com.dkd.manage.domain.TaskDetails;
import com.dkd.manage.domain.VendingMachine;
import com.dkd.manage.domain.dto.TaskDetailsDto;
import com.dkd.manage.domain.dto.TaskDto;
import com.dkd.manage.domain.vo.TaskVo;
import com.dkd.manage.service.IEmpService;
import com.dkd.manage.service.ITaskDetailsService;
import com.dkd.manage.service.IVendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.dkd.manage.mapper.TaskMapper;
import com.dkd.manage.domain.Task;
import com.dkd.manage.service.ITaskService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工单Service业务层处理
 * 
 * @author itheima
 * @date 2025-03-17
 */
@Service
public class TaskServiceImpl implements ITaskService 
{
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private IVendingMachineService vendingMachineService;

    @Autowired
    private IEmpService empService;

    @Autowired
    private ITaskDetailsService taskDetailsService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询工单
     * 
     * @param taskId 工单主键
     * @return 工单
     */
    @Override
    public Task selectTaskByTaskId(Long taskId)
    {
        return taskMapper.selectTaskByTaskId(taskId);
    }

    /**
     * 查询工单列表
     * 
     * @param task 工单
     * @return 工单
     */
    @Override
    public List<Task> selectTaskList(Task task)
    {
        return taskMapper.selectTaskList(task);
    }

    /**
     * 新增工单
     * 
     * @param task 工单
     * @return 结果
     */
    @Override
    public int insertTask(Task task)
    {
        task.setCreateTime(DateUtils.getNowDate());
        return taskMapper.insertTask(task);
    }

    /**
     * 修改工单
     * 
     * @param task 工单
     * @return 结果
     */
    @Override
    public int updateTask(Task task)
    {
        task.setUpdateTime(DateUtils.getNowDate());
        return taskMapper.updateTask(task);
    }

    /**
     * 批量删除工单
     * 
     * @param taskIds 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskIds(Long[] taskIds)
    {
        return taskMapper.deleteTaskByTaskIds(taskIds);
    }

    /**
     * 删除工单信息
     * 
     * @param taskId 工单主键
     * @return 结果
     */
    @Override
    public int deleteTaskByTaskId(Long taskId)
    {
        return taskMapper.deleteTaskByTaskId(taskId);
    }

    /**
     * 查询运维工单列表
     *
     * @param task
     * @return TaskVo集合
     */
    @Override
    public List<TaskVo> selectTaskVoList(Task task) {
        return taskMapper.selectTaskVoList(task);
    }

    /**
     * 新增运营、运维工单
     *
     * @param taskDto
     * @return 结果
     */
    @Override
    @Transactional
    public int insertTaskDto(TaskDto taskDto) {
        //1.查询售货机是否存在
        VendingMachine vm = vendingMachineService.selectVendingMachineByInnerCode(taskDto.getInnerCode());
        if (vm == null) {
            throw new ServiceException("设备不存在");
        }
        //2.校验同类型工单是否符合要求
        checkCreateTask(taskDto.getProductTypeId(), vm.getVmStatus());
        //3. 检查设备是否有未完成的同类型工单
        hasTask(taskDto);
        //4.校验员工
        Emp emp = empService.selectEmpById(taskDto.getUserId());
        if(emp == null){
            throw new ServiceException("员工不存在");
        }
        //5.校验员工区域是否匹配
        if(!emp.getRegionId().equals(vm.getRegionId())){
            throw new ServiceException("员工区域不匹配");
        }
        //6.将dto转换po并补充属性，，进行保存
        Task task = BeanUtil.copyProperties(taskDto, Task.class);//复制属性
        task.setTaskStatus(DkdContants.TASK_STATUS_CREATE);//默认为创建工单
        task.setUserName(emp.getUserName());//执行人名称
        task.setRegionId(vm.getRegionId());//所属区域id
        task.setAddr(vm.getAddr());//地址
        task.setCreateTime(DateUtils.getNowDate());//设置创建时间
        String taskCode = generateTaskCode();
        System.out.println(taskCode);System.out.println(11111);System.out.println(11111);System.out.println(11111);
        if (StringUtils.isEmpty(taskCode)) {
            throw new ServiceException("工单编号生成失败");
        }
        task.setTaskCode(taskCode);
        //task.setTaskCode(generateTaskCode());//工单编号
        int taskResult = taskMapper.insertTask(task);
        //7.判断是否为补货工单
        if(taskDto.getProductTypeId().equals( DkdContants.TASK_TYPE_SUPPLY)){
            // 8.保存工单详情
            List<TaskDetailsDto> details = taskDto.getDetails();
            if (CollUtil.isEmpty(details)) {
                throw new ServiceException("补货工单详情不能为空");
            }
            // 将dto转为po补充属性
            List<TaskDetails> list = details.stream().map(dto -> {
                TaskDetails taskDetails = BeanUtil.copyProperties(dto, TaskDetails.class);//复制属性
                taskDetails.setTaskId(task.getTaskId());//工单id
                return  taskDetails;
            }).collect(Collectors.toList());
            taskDetailsService.batchInsertTaskDetails(list);
        }
        return taskResult;
    }

    /**
     * 取消工单
     * @param task
     * @return 结果
     */
    @Override
    public int cancelTask(Task task) {
        //1. 判断工单状态是否可以取消
        // 先根据工单id查询数据库
        Task taskDb = taskMapper.selectTaskByTaskId(task.getTaskId());
        // 判断工单状态是否为已取消，如果是，则抛出异常
        if (taskDb.getTaskStatus().equals(DkdContants.TASK_STATUS_CANCEL)) {
            throw new ServiceException("该工单已取消了，不能再次取消");
        }
        // 判断工单状态是否为已完成，如果是，则抛出异常
        if (taskDb.getTaskStatus().equals(DkdContants.TASK_STATUS_FINISH)) {
            throw new ServiceException("该工单已完成了，不能取消");
        }
        //2. 设置更新字段
        task.setTaskStatus(DkdContants.TASK_STATUS_CANCEL);// 工单状态：取消
        task.setUpdateTime(DateUtils.getNowDate());// 更新时间
        //3. 更新工单
        return taskMapper.updateTask(task);// 注意别传错了，这里是前端task参数
    }

    public String generateTaskCode() {
        // 获取当前日期并格式化为"yyyyMMdd"
        String dateStr = DateUtils.getDate().replaceAll("-", "");
        // 根据日期生成redis的键
        String key = "dkd.task.code." + dateStr;
        // 判断key是否存在
        if (!redisTemplate.hasKey(key)) {
            // 如果key不存在，设置初始值为1，并指定过期时间为1天
            redisTemplate.opsForValue().set(key, 1, Duration.ofDays(1));
            // 返回工单编号（日期+0001）
            return dateStr + "0001";
        }
        // 如果key存在，计数器+1（0002），确保字符串长度为4位
        return dateStr+StrUtil.padPre(redisTemplate.opsForValue().increment(key).toString(),4,'0');
    }

    private void hasTask(TaskDto taskDto) {
        Task task = new Task();
        task.setInnerCode(taskDto.getInnerCode());
        task.setProductTypeId(taskDto.getProductTypeId());
        task.setTaskStatus(DkdContants.TASK_STATUS_PROGRESS);
        List<Task> taskList = taskMapper.selectTaskList(task);
        // 如果存在未完成的同类型工单，则抛出服务异常
        if (CollUtil.isNotEmpty(taskList)) {
            throw new ServiceException("该设备有未完成的同类型工单，不能重复创建");
        }
    }

    private static void checkCreateTask(Long productTypeId, Long vmStatus) {
        // 如果是投放工单，且设备状态为运行中，则抛出异常，因为设备已在运营中无法进行投放
        if (productTypeId == DkdContants.TASK_TYPE_DEPLOY && vmStatus == DkdContants.VM_STATUS_RUNNING) {
            throw new ServiceException("该设备状态为运行中，无法进行投放");
        }

        // 如果是维修工单，且设备状态不是运行中，则抛出异常，因为设备不在运营中无法进行维修
        if (productTypeId == DkdContants.TASK_TYPE_REPAIR && vmStatus != DkdContants.VM_STATUS_RUNNING) {
            throw new ServiceException("该设备状态不是运行中，无法进行维修");
        }

        // 如果是补货工单，且设备状态不是运行中，则抛出异常，因为设备不在运营状态无法进行补货
        if (productTypeId == DkdContants.TASK_TYPE_SUPPLY && vmStatus != DkdContants.VM_STATUS_RUNNING) {
            throw new ServiceException("该设备状态不是运行中，无法进行补货");
        }

        // 如果是撤机工单，且设备状态不是运行中，则抛出异常，因为设备不在运营状态无法进行撤机
        if (productTypeId == DkdContants.TASK_TYPE_REVOKE && vmStatus != DkdContants.VM_STATUS_RUNNING) {
            throw new ServiceException("该设备状态不是运行中，无法进行撤机");
        }
    }
}
