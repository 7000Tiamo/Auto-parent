-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表', '2019', '1', 'emp', 'manage/emp/index', 1, 0, 'C', '0', '0', 'manage:emp:list', '#', 'admin', sysdate(), '', null, '人员列表菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'manage:emp:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'manage:emp:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'manage:emp:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'manage:emp:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('人员列表导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'manage:emp:export',       '#', 'admin', sysdate(), '', null, '');