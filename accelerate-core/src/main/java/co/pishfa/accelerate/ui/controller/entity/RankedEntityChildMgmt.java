package co.pishfa.accelerate.ui.controller.entity;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.entity.common.RankedEntity;
import co.pishfa.accelerate.ui.UiAction;

import java.util.List;

/**
 * Keeps continues rank among child
 * @author Taha Ghasemi.
 */
abstract public class RankedEntityChildMgmt<T extends RankedEntity<Long>, P extends Entity<Long>> extends EntityChildMgmt<T,P> {

    private int maxRank,prevRank; //keep track of rank change

    public int getMaxRank() {
        return maxRank;
    }

    @Override
    protected List<T> findData() {
        List<T> res = super.findData();
        if(res != null && !res.isEmpty()) {
            maxRank = res.get(res.size()-1).getRank();
        } else
            maxRank = 0;
        return res;
    }

    @Override
    protected T newEntity() {
        T entity = super.newEntity();
        entity.setRank(++maxRank);
        return entity;
    }

    @Override
    protected void addEdit() {
        super.addEdit();
        prevRank = getCurrent().getRank();
    }

    @Override
    public String save() {
        if(prevRank != getCurrent().getRank()) {
            getData().remove(getCurrent());
            getData().add(getCurrent().getRank()-1,getCurrent());
            for(int i = 0; i < getData().size(); i++)
                getData().get(i).setRank(i+1);
            maxRank = getData().size();
        }
        return super.save();
    }

    public boolean canUp(T entity) {
        return getData().indexOf(entity) > 0;
    }

    @UiAction
    public String up() {
        int pos = getData().indexOf(getCurrent());
        T des = getData().get(pos-1);
        int r = des.getRank();
        des.setRank(getCurrent().getRank());
        getCurrent().setRank(r);
        //update list
        getData().set(pos, des);
        getData().set(pos - 1, getCurrent());
        return null;
    }

    public boolean canDown(T entity) {
        return getData().indexOf(entity) < getData().size()-1;
    }

    @UiAction
    public String down() {
        int pos = getData().indexOf(getCurrent());
        T des = getData().get(pos+1);
        int r = des.getRank();
        des.setRank(getCurrent().getRank());
        getCurrent().setRank(r);
        //update list
        getData().set(pos, des);
        getData().set(pos + 1, getCurrent());
        return null;
    }

    @Override
    protected void deleteEntity(T entity) {
        super.deleteEntity(entity);
        //reset ranks
        if(getData() != null) {
            for(int i = 0; i < getData().size(); i++)
                getData().get(i).setRank(i+1);
        }
        maxRank = getData().size();
    }
}
