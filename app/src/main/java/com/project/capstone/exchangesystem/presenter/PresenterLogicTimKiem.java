package com.project.capstone.exchangesystem.presenter;

import com.project.capstone.exchangesystem.model.Item;
import com.project.capstone.exchangesystem.model.ModelTimKiem;
import com.project.capstone.exchangesystem.view.ViewTimKiem;

import java.util.ArrayList;
import java.util.List;

public class PresenterLogicTimKiem implements IPresenterTimKiem {

    ViewTimKiem viewTimKiem;
    ModelTimKiem modelTimKiem;

    public PresenterLogicTimKiem() {
    }

    public PresenterLogicTimKiem(ViewTimKiem viewTimKiem) {
        this.viewTimKiem = viewTimKiem;
        modelTimKiem = new ModelTimKiem();
    }

    @Override
    public void TimKiemSanPhamTheoTenSP(String tensp, int limit) {

        ArrayList<Item> sanPhamList = modelTimKiem.TimKiemSanPhamTheoTen(tensp, "DANHSACHSANPHAM", "dsad", 0);

        if (sanPhamList.size() > 0) {
            viewTimKiem.TimKiemThanhCong(sanPhamList);
        } else {
            viewTimKiem.TimKiemThatBai();
        }
    }
}
