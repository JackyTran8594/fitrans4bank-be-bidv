package com.eztech.fitrans.repo.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.dto.response.MenuRoleTreeDTO;
import com.eztech.fitrans.dto.response.RoleDTO;
import com.eztech.fitrans.dto.response.RoleListDTO;
import com.eztech.fitrans.dto.response.RoleTreeDTO;
import com.eztech.fitrans.model.Role;
import com.eztech.fitrans.model.RoleList;
import com.eztech.fitrans.repo.RoleRepositoryCustom;
import com.eztech.fitrans.util.BaseMapper;
import com.eztech.fitrans.util.DataUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleRepositoryCustomImpl extends BaseCustomRepository<Role> implements
        RoleRepositoryCustom {
    private static final BaseMapper<RoleList, RoleListDTO> mapper = new BaseMapper<>(RoleList.class, RoleListDTO.class);
    private static final BaseMapper<MenuRoleTreeDTO, MenuRoleTreeDTO> menuMapper = new BaseMapper<>(
            MenuRoleTreeDTO.class, MenuRoleTreeDTO.class);

    @Override
    public List search(Map searchDTO, Class aClass) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, false);
        return getResultList(sql, Role.class, parameters);
    }

    @Override
    public Long count(Map searchDTO) {
        Map<String, Object> parameters = new HashMap<>();
        String sql = buildQuery(searchDTO, parameters, true);
        return getCountResult(sql, parameters);
    }

    @Override
    public Integer updateStatus(Long id, String status, String lastUpdatedBy,
            LocalDateTime lastUpdateDate) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        sb.append(
                "UPDATE role SET status =:status, last_updated_by = :updateBy,last_updated_date=:updateDate WHERE id = :id ");
        parameters.put("id", id);
        parameters.put("status", status);
        parameters.put("updateBy", lastUpdatedBy);
        parameters.put("updateDate", lastUpdateDate);
        return executeUpdate(sb.toString(), parameters);
    }

    @Override
    public Boolean checkExits(Long id, String code) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        sb.append("SELECT COUNT(*) FROM role WHERE 1=1 ");
        if (DataUtils.notNull(id)) {
            sb.append(" AND id != :id ");
            parameters.put("id", id);
        }
        if (DataUtils.notNullOrEmpty(code)) {
            sb.append(" AND UPPER(code) = :code ");
            parameters.put("code", code.trim().toUpperCase());
        }
        sb.append(" AND status > 0");
        return getCountResult(sb.toString(), parameters) > 0L;
    }

    @Override
    public String buildQuery(Map<String, Object> paramSearch, Map<String, Object> parameters,
            boolean count) {
        StringBuilder sb = new StringBuilder();
        if (count) {
            sb.append("SELECT COUNT(id) \n")
                    .append("FROM role os\n")
                    .append("WHERE 1=1 ");
        } else {
            sb.append("SELECT os.* \n")
                    .append("FROM role os\n")
                    .append("WHERE 1=1 ");
        }

        if (paramSearch.containsKey("id")) {
            sb.append(" AND os.id = :id ");
            parameters.put("id", DataUtils.parseToLong(paramSearch.get("id")));
        }

        if (paramNotNullOrEmpty(paramSearch, "code")) {
            sb.append(" AND UPPER(os.code) LIKE :code ");
            parameters.put("code", formatLike((String) paramSearch.get("code")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "name")) {
            sb.append(" AND UPPER(os.name) LIKE :name ");
            parameters.put("name", formatLike((String) paramSearch.get("name")).toUpperCase());
        }

        if (paramNotNullOrEmpty(paramSearch, "status")) {
            sb.append(" AND os.status = :status ");
            parameters.put("status", paramSearch.get("status"));
        }

        if (paramNotNullOrEmpty(paramSearch, "description")) {
            sb.append(" AND UPPER(os.description) LIKE :description ");
            parameters
                    .put("description", formatLike((String) paramSearch.get("description")).toUpperCase());
        }

        if (!count) {
            if (paramSearch.containsKey("sort")) {
                sb.append(formatSort((String) paramSearch.get("sort"), " ORDER BY os.code ASC  "));
            } else {
                sb.append(" ORDER BY os.id desc ");
            }
        }

        if (!count && paramNotNullOrEmpty(paramSearch, "pageSize") && !"0"
                .equalsIgnoreCase(String.valueOf(paramSearch.get("pageSize")))) {
            sb.append(" OFFSET :offset ROWS ");
            sb.append(" FETCH NEXT :limit ROWS ONLY ");
            parameters.put("offset", offetPaging(DataUtils.parseToInt(paramSearch.get("pageNumber")),
                    DataUtils.parseToInt(paramSearch.get("pageSize"))));
            parameters.put("limit", DataUtils.parseToInt(paramSearch.get("pageSize")));
        }
        return sb.toString();
    }

    @Override
    public List<RoleTreeDTO> mapRoleList() {
        Map<String, Object> parameters = new HashMap<>();
        String sql = "SELECT * FROM role_list order by parent_code asc";
        List<RoleList> list = getResultList(sql, RoleList.class, parameters);
        if (DataUtils.isNullOrEmpty(list)) {
            return new ArrayList<>();
        }

        Map<String, List<RoleListDTO>> map = new HashMap<>();
        for (RoleList roleList : list) {
            RoleListDTO dto = mapper.toDtoBean(roleList);
            List<RoleListDTO> listDTOList = map.get(dto.getParentCode());
            if (DataUtils.isNullOrEmpty(listDTOList)) {
                listDTOList = new ArrayList<>();
            }
            listDTOList.add(dto);
            map.put(dto.getParentCode(), listDTOList);
        }

        List<RoleTreeDTO> rtn = new ArrayList<>();
        for (Map.Entry<String, List<RoleListDTO>> entry : map.entrySet()) {
            RoleTreeDTO roleTreeDTO = new RoleTreeDTO();
            roleTreeDTO.setDescription(entry.getKey());
            roleTreeDTO.setName(entry.getKey());
            roleTreeDTO.setChildren(entry.getValue());
            rtn.add(roleTreeDTO);
        }
        return rtn;
    }

    @Override
    public List<RoleTreeDTO> mapMenuRole() {
        // TODO Auto-generated method stub
        Map<String, Object> parameters = new HashMap<>();
        // menu_recursive
        String sql = "WITH menu_recursive AS ( \n" +
                " SELECT rl.menu, rl.menu_name, rl.parent_code, rl.code, rl.description \n" +
                "FROM [role_list] AS rl \n" +
                "WHERE rl.parent_code IS NULL OR rl.parent_code = '' \n" +
                "UNION all \n" +
                "select rl2.menu, rl2.menu_name,rl2.parent_code, rl2.code, rl2.description \n" +
                "FROM role_list AS rl2 \n" +
                "INNER JOIN menu_recursive AS mr ON mr.menu = rl2.parent_code) \n" +
                "SELECT * FROM menu_recursive";

        List<MenuRoleTreeDTO> menuRoles = getResultList(sql, Constants.ResultSetMapping.MENU_ROLE_DTO, parameters);
        if (DataUtils.isNullOrEmpty(menuRoles)) {
            return new ArrayList<>();
        }

        List<MenuRoleTreeDTO> menu = new ArrayList<>();
        List<MenuRoleTreeDTO> subMenu = new ArrayList<>();
        List<MenuRoleTreeDTO> subMenu2 = new ArrayList<>();
        for (MenuRoleTreeDTO item : menuRoles) {
            // MenuRoleTreeDTO dto = menuMapper.toDtoBean(item);
            if (DataUtils.isNullOrEmpty(item.getParentCode())) {
                MenuRoleTreeDTO tree = new MenuRoleTreeDTO();
                menu.add(tree);
            }              
            
        }

        for (MenuRoleTreeDTO parent : menu) {
            List<MenuRoleTreeDTO> lstSub = new ArrayList<MenuRoleTreeDTO>();
            for (MenuRoleTreeDTO sub : subMenu) {
                if (parent.getMenu().equals(sub.getParentCode()) && !DataUtils.isNullOrEmpty(sub.getParentCode())
                        && !DataUtils.isNullOrEmpty(parent.getMenu())) {
                    lstSub.add(sub);
                }

            }
            // parent.setSubMenu(lstSub);
        }
        List<RoleTreeDTO> lst = new ArrayList<RoleTreeDTO>();
        return lst;
    }
}
