package com.pinyougou.service;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author 550185890@qq.com
 * @Date 2019年9月26日17:19:51
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("-----调用认证类：--------UserDetailsServiceImpl----------------");
        List<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
        authorities.add(grantedAuthority);
        TbSeller tbSeller = sellerService.findOne(username);
        if(tbSeller!=null){
            //审核通过的才可以登录
            if(tbSeller.getStatus().equals("1")){
                return new User(username,tbSeller.getPassword(),authorities);
            }else{
                return null;
            }
        }else{
            return null;
        }

    }
}
