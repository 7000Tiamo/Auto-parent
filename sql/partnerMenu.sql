-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商', '2000', '1', 'partner', 'manage/partner/index', 1, 0, 'C', '0', '0', 'manage:partner:list', '#', 'admin', sysdate(), '', null, '合作商菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'manage:partner:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'manage:partner:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'manage:partner:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'manage:partner:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('合作商导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'manage:partner:export',       '#', 'admin', sysdate(), '', null, '');