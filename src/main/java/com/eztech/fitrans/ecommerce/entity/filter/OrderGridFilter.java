package com.eztech.fitrans.ecommerce.entity.filter;

import com.eztech.fitrans.user.BaseFilter;

@SuppressWarnings("checkstyle:sizes")
public class OrderGridFilter extends BaseFilter {
    //we violate naming convention because we use this names in ng2SmartTable
    private String filterByname;
    private String filterBydate;
    private String filterBysum;
    private String filterBytype;
    private String filterBystatus;
    private String filterBycountry;

    public String getFilterByname() {
        return filterByname;
    }

    public void setFilterByname(String filterByname) {
        this.filterByname = filterByname;
    }

    public String getFilterBydate() {
        return filterBydate;
    }

    public void setFilterBydate(String filterBydate) {
        this.filterBydate = filterBydate;
    }

    public String getFilterBysum() {
        return filterBysum;
    }

    public void setFilterBysum(String filterBysum) {
        this.filterBysum = filterBysum;
    }

    public String getFilterBytype() {
        return filterBytype;
    }

    public void setFilterBytype(String filterBytype) {
        this.filterBytype = filterBytype;
    }

    public String getFilterBystatus() {
        return filterBystatus;
    }

    public void setFilterBystatus(String filterBystatus) {
        this.filterBystatus = filterBystatus;
    }

    public String getFilterBycountry() {
        return filterBycountry;
    }

    public void setFilterBycountry(String filterBycountry) {
        this.filterBycountry = filterBycountry;
    }
}
