package com.kinny.shop.realm;

import com.alibaba.dubbo.config.annotation.Reference;
import com.kinny.common.pojo.em.SellerEnum;
import com.kinny.pojo.TbSeller;
import com.kinny.sellergoods.service.SellerService;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

/**
 * @author qgy
 * @create 2019/5/12 - 10:45
 */
public class ShiroRealm extends AuthenticatingRealm {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        String username = usernamePasswordToken.getUsername();

        TbSeller seller = this.sellerService.findOne(username);

        if (seller == null) {
            throw new UnknownAccountException("商家账号不存在");
        }

        // 商家状态
        if(!SellerEnum.CHECKED.getCode().equals(seller.getStatus())) {
            throw new LockedAccountException("商家未审核");
        }

        ByteSource credentialsSalt = ByteSource.Util.bytes(seller.getSellerId());
        AuthenticationInfo authenticationInfo =
                new SimpleAuthenticationInfo(seller, seller.getPassword(), credentialsSalt, this.getName());

        return authenticationInfo;
    }
}
