package com.csgo.condition.stageProperty;

import com.csgo.condition.Condition;
import com.csgo.domain.plus.stageProperty.ProductFilterCategoryPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author admin
 */
@Getter
@Setter
public class SearchProductFilterCategoryPlusCondition extends Condition<ProductFilterCategoryPlus> {

    private String name;
}
