package io.github.MigadaTang;

import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.dao.LayoutInfoDAO;
import io.github.MigadaTang.entity.LayoutInfoDO;
import io.github.MigadaTang.exception.ERException;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.List;

/**
 * The layout information of a component on the ER diagram
 * <p>Each component does not have a layoutInfo after creation,
 * layoutInfo is given to a component when the layoutInfo of this component is being updated</p>
 */
@Getter
public class LayoutInfo {
    /**
     * The ID of the layoutInfo
     */
    private Long ID;
    /**
     * The ID of the object to which this layoutInfo belongs
     */
    private Long belongObjID;
    /**
     * The type of the object to which this layoutInfo belongs
     */
    private BelongObjType belongObjType;
    /**
     * The x-axis of the center of the component on the diagram
     */
    private Double layoutX;

    /**
     * The y-axis of the center of the component on the diagram
     */
    private Double layoutY;

    public LayoutInfo(Long ID, Long belongObjID, BelongObjType belongObjType, Double layoutX,
        Double layoutY) {
        this.ID = ID;
        this.belongObjID = belongObjID;
        this.belongObjType = belongObjType;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        if (this.ID == 0) {
            this.insertDB();
        }
    }

    /**
     * Query the list of layoutInfo that have the same data specified by the layoutInfoDO
     *
     * @param layoutInfoDO values of some attributes of an layoutInfo
     * @return A list of layoutInfo
     */
    public static List<LayoutInfo> query(LayoutInfoDO layoutInfoDO) {
        List<LayoutInfoDO> LayoutInfoDOList = LayoutInfoDAO.selectByLayoutInfo(layoutInfoDO);
        return ObjConv.ConvLayoutInfoListFormDB(LayoutInfoDOList);
    }

    /**
     * Query the list of layoutInfo
     *
     * @param relatedObjID  the ID of the object to which a layoutInfo belongs
     * @param belongObjType the type of the object to which a layoutInfo belongs
     * @return the found layoutInfo
     */
    public static LayoutInfo queryByObjIDAndObjType(Long relatedObjID, BelongObjType belongObjType) {
        List<LayoutInfo> layoutInfoDOList = query(new LayoutInfoDO(relatedObjID, belongObjType));
        if (layoutInfoDOList.size() != 0) {
            return layoutInfoDOList.get(0);
        }
        return null;
    }

    /**
     * Query the list of layoutInfo that have the same data specified by the layoutInfoDO
     *
     * @param ID the ID of the layoutInfo
     * @return the found layoutInfo
     */
    public static LayoutInfo queryByID(Long ID) throws ERException {
        List<LayoutInfo> layoutInfoDOList = query(new LayoutInfoDO(ID));
        if (layoutInfoDOList.size() == 0) {
            throw new ERException(String.format("LayoutInfo with ID: %d not found ", ID));
        } else {
            return layoutInfoDOList.get(0);
        }
    }

    private void insertDB() {
        try {
            LayoutInfoDO LayoutInfoDO = new LayoutInfoDO(0L, this.belongObjID, this.belongObjType, this.layoutX, this.layoutY);
            int ret = LayoutInfoDAO.insert(LayoutInfoDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = LayoutInfoDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    /**
     * Update the x-axis and y-axis of the position
     *
     * @param layoutX the x-axis
     * @param layoutY the y-axis
     * @throws ERException throws if no record is found
     */
    public void update(Double layoutX, Double layoutY) throws ERException {
        if (layoutX != null) {
            this.layoutX = layoutX;
        }
        if (layoutX != null) {
            this.layoutY = layoutY;
        }
        int ret = LayoutInfoDAO.updateByID(new LayoutInfoDO(this.ID, this.belongObjID, this.belongObjType, this.layoutX, this.layoutY));
        if (ret == 0) {
            throw new ERException(String.format("cannot find LayoutInfo with ID: %d", this.ID));
        }
    }
}
