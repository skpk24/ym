package com.ilinks.restful;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.ilinks.restful.catalog.*;
import com.ilinks.restful.cart.*;
import com.ilinks.restful.category.*;
import com.ilinks.restful.post.ILinksServiceResource;
import com.ilinks.restful.product.*;
import com.ilinks.restful.login.*;
import com.ilinks.restful.profile.*;
import com.ilinks.restful.checkout.*;
import com.ilinks.restful.brand.*;
import com.ilinks.restful.logging.*;
import com.ilinks.restful.fedex.*;
import com.ilinks.restful.shoppinglist.*;
import com.ilinks.restful.referAFriend.*;
import com.ilinks.restful.order.*;

public class OFBizApplication extends Application {
  @Override
  public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(CatalogResource.class);
      classes.add(CartResource.class);
      classes.add(CategoryResource.class);
      classes.add(ProductResource.class);
      classes.add(LoginResource.class);
      classes.add(ProfileResource.class);
      classes.add(CheckOutResource.class);
      classes.add(BrandResource.class);
      classes.add(LoggingResource.class);
      classes.add(FedExResource.class);
      classes.add(ShoppingListResource.class);
      classes.add(ReferAFriendResource.class);
      classes.add(OrderResource.class);
      classes.add(ILinksServiceResource.class);
      return classes;
  }
}