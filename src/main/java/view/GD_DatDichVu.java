package view;

import dao.ChiTietDatDichVuDAO;
import dao.DichVuDAO;
import dao.HoaDonDAO;
import dao.PhieuDatPhongDAO;
import entity.*;
import enums.TrangThaiDichVu;
import utils.DichVuPanelClickListener;
import utils.RoomPanelUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GD_DatDichVu extends JFrame implements DichVuPanelClickListener, ActionListener {

    private static final long serialVersionUID = 1L;
    private final DefaultTableModel model;
    private JPanel panel;
    private JPanel services;
    private final JButton btnFind, btnChange, btnDelete, btnClearAll, btnApply;
    private JTextField txtServiceName;
    private JTable tableOrderedServices;
    private JTextField textField;
    private JComboBox cmbLoaiDV;
    private List<DichVu> listDichVu = new ArrayList<>();
    private final DichVuDAO dichVuDAO;
    private List<DichVu> selectedDichVuList = new ArrayList<>();
    private final ChiTietDatDichVuDAO chiTietDatDichVuDAO;
    private final PhieuDatPhongDAO phieuDatPhongDAO;
    private final HoaDon hoaDon;
    private final HoaDonDAO hoaDonDAO;
    private int preQuantity, curTempQuantity, quantity = 0, curQuantity;
    private DichVu dichVu;
    private Phong phong;
    private List<ChiTietDatDichVu> listCTDV = new ArrayList<>();

    public GD_DatDichVu(HoaDon selectedHoaDon, Phong phong) {
        hoaDon = selectedHoaDon;
        dichVuDAO = new DichVuDAO();
        this.phong = phong;
        chiTietDatDichVuDAO = new ChiTietDatDichVuDAO();
        phieuDatPhongDAO = new PhieuDatPhongDAO();
        hoaDonDAO = new HoaDonDAO();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel pnTitle = new JPanel();
        pnTitle.setBackground(new Color(97, 255, 204));
        getContentPane().add(pnTitle, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Đặt dịch vụ");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 25));
        pnTitle.add(lblTitle);

        panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel lblRoomName = new JLabel("Phòng: " + phong.getTenPhong());
        lblRoomName.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblRoomName.setBounds(10, 10, 200, 25);
        panel.add(lblRoomName);

        JLabel lblServiceType = new JLabel("Loại dịch vụ");
        lblServiceType.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblServiceType.setBounds(369, 10, 96, 25);
        panel.add(lblServiceType);

        cmbLoaiDV = new JComboBox();
        cmbLoaiDV.setBounds(475, 12, 85, 25);
        panel.add(cmbLoaiDV);
        cmbLoaiDV.addItem("Tất cả");

        List<LoaiDichVu> listLoaiDV = dichVuDAO.getLoaiDichVu();
        for (LoaiDichVu loaiDichVu : listLoaiDV) {
            cmbLoaiDV.addItem(loaiDichVu.getTenLoaiDichVu());
        }

        cmbLoaiDV.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (cmbLoaiDV.getSelectedIndex() == 0) {
                        loadDichVu(dichVuDAO.getAllDichVu());
                    } else if (cmbLoaiDV.getSelectedIndex() == 1) {
                        loadDichVu(dichVuDAO.getDSTheoLoai(cmbLoaiDV.getSelectedItem().toString()));
                    } else if (cmbLoaiDV.getSelectedIndex() == 2) {
                        loadDichVu(dichVuDAO.getDSTheoLoai(cmbLoaiDV.getSelectedItem().toString()));
                    }
                }
            }
        });

        JLabel lblServiceName = new JLabel("Tên dịch vụ:");
        lblServiceName.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblServiceName.setBounds(570, 10, 96, 25);
        panel.add(lblServiceName);

        txtServiceName = new JTextField();
        txtServiceName.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtServiceName.setBounds(664, 10, 162, 25);
        panel.add(txtServiceName);
        txtServiceName.setColumns(10);

        btnFind = new JButton("Tìm kiếm");
        btnFind.setBackground(new Color(107, 208, 107));
        btnFind.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnFind.setBounds(858, 10, 117, 25);
        panel.add(btnFind);

        String row[] = {"STT", "Tên dịch vụ", "Số lượng", "Thành tiền"};
        model = new DefaultTableModel(row, 0);
        tableOrderedServices = new JTable(model);
        JScrollPane spOrderedServices = new JScrollPane(tableOrderedServices);
        spOrderedServices.setBounds(10, 58, 327, 479);
        panel.add(spOrderedServices);

        initData();
        createListDichVu();

        btnChange = new JButton("Thay đổi");
        btnChange.setBackground(new Color(107, 208, 107));
        btnChange.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnChange.setBounds(10, 571, 94, 25);
        panel.add(btnChange);

        btnDelete = new JButton("Xóa");
        btnDelete.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnDelete.setBackground(new Color(221, 78, 78));
        btnDelete.setBounds(123, 571, 94, 25);
        panel.add(btnDelete);

        btnClearAll = new JButton("Xóa tất cả");
        btnClearAll.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnClearAll.setBackground(new Color(57, 130, 240));
        btnClearAll.setBounds(243, 571, 94, 25);
        panel.add(btnClearAll);

        JLabel lblTotalPrice = new JLabel("Tổng tiền:");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTotalPrice.setBounds(511, 573, 82, 25);
        panel.add(lblTotalPrice);

        textField = new JTextField();
        textField.setBounds(620, 572, 185, 25);
        panel.add(textField);
        textField.setColumns(10);

        btnApply = new JButton("Đặt dịch vụ");
        btnApply.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnApply.setBackground(new Color(107, 208, 107));
        btnApply.setBounds(855, 570, 120, 25);
        panel.add(btnApply);

        btnApply.addActionListener(this);
        btnChange.addActionListener(this);
        btnFind.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClearAll.addActionListener(this);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                clearAllData();
            }
        });

        loadChiTietDichVu();
        updateOrderedServicesTable();
//        System.out.println(listDichVu);
    }

    public void initData() {
        listDichVu = dichVuDAO.getAllDichVu();
    }

    private void createListDichVu() {
        services = new JPanel();
        JScrollPane spServices = new JScrollPane(services);
        JScrollBar verticalScrollbar = spServices.getVerticalScrollBar();
        verticalScrollbar.setUnitIncrement(16);
        services.setLayout(new GridLayout(0, 3, 0, 0));
        spServices.setBounds(369, 58, 606, 479);
        panel.add(spServices);

        loadDichVu(this.listDichVu);
    }

    private void loadDichVu(List<DichVu> listDichVu) {
        services.removeAll();
        List<JPanel> dichVuPanel = RoomPanelUtil.createDichVuPanels(listDichVu, this);
        dichVuPanel.forEach(services::add);

        services.revalidate();
        services.repaint();
    }

    @Override
    public void onDichVuPanelClicked(DichVu dichVu) {
        String quantityStr = JOptionPane.showInputDialog(this, "Nhập số lượng dịch vụ muốn đặt:", "Nhập số lượng", JOptionPane.QUESTION_MESSAGE);
        DichVu dichVu1 = dichVuDAO.getDichVuTheoMa(dichVu.getMaDichVu()).get(0);
        preQuantity = dichVu1.getSoLuong();

        if (quantityStr == null) {
            return;
        } else {
            quantity = Integer.parseInt(quantityStr);
            int quantityAtBegin = curTempQuantity + preQuantity;

            try {
                if (quantity > quantityAtBegin) {
                    JOptionPane.showMessageDialog(this, "Số lượng đặt vượt quá số lượng hiện tại của sản phẩm.\nSố lượng hiện tại: " + quantityAtBegin, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    handleDeleteTask();

                    quantity = Integer.parseInt(quantityStr);
                    preQuantity = dichVu.getSoLuong();

                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(this, "Số lượng phải là một số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    curQuantity = dichVu.getSoLuong();

                    dichVu.setSoLuong(quantity);
                    DichVu newDichVu = dichVu;
                    selectedDichVuList.add(newDichVu);

                    this.dichVu = newDichVu;

                    updateQuantity(newDichVu);
                    newDichVu.setSoLuong(quantity);
                    updateOrderedServicesTable();
                    curTempQuantity = dichVu1.getSoLuong();
                    quantity = 0;
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số nguyên dương.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    private void updateQuantity(DichVu dichVu) {
        curQuantity = dichVuDAO.getDichVuTheoMa(dichVu.getMaDichVu()).get(0).getSoLuong();
//        dichVu.setSoLuong(curQuantity - quantity);
//        dichVuDAO.updateDichVu(dichVu, dichVu.getMaDichVu());
        insertChiTietDatDichVu();
        listDichVu = dichVuDAO.getAllDichVu();
        loadDichVu(listDichVu);
    }

    private void updateOrderedServicesTable() {
        model.setRowCount(0);

        int stt = 1;
        for (DichVu dichVu : selectedDichVuList) {
            Object[] rowData = {stt++, dichVu.getTenDichVu(), dichVu.getSoLuong(), dichVu.getLichSuGiaDichVuList().get(0).getGia()};
            model.addRow(rowData);
        }
        updateTotalPrice();
        this.quantity = 0;
    }

    private void loadChiTietDichVu() {
        listCTDV = chiTietDatDichVuDAO.getChiTietDatDichVuByPhieuDatPhong(phieuDatPhongDAO.getPhieuDatPhongByMaHoaDon(hoaDon.getMaHoaDon()).get(0).getMaPhieuDatPhong());
        for (ChiTietDatDichVu chiTietDatDichVu : listCTDV) {

            String maDV = chiTietDatDichVu.getDichVu().getMaDichVu();
            String tenDV = chiTietDatDichVu.getDichVu().getTenDichVu();
            int soLuong = chiTietDatDichVu.getSoLuong();
            TrangThaiDichVu trangThai = chiTietDatDichVu.getDichVu().getTrangThai();
            LoaiDichVu loaiDichVu = chiTietDatDichVu.getDichVu().getLoaiDichVu();
            LichSuGiaDichVu lichSuGiaDichVu = chiTietDatDichVu.getDichVu().getLichSuGiaDichVuList().get(0);
            List<LichSuGiaDichVu> lichSuGiaDichVuList = new ArrayList<>();
            lichSuGiaDichVuList.add(lichSuGiaDichVu);

            DichVu dichVu1 = new DichVu(maDV, tenDV, soLuong, trangThai, loaiDichVu, lichSuGiaDichVuList);
            selectedDichVuList.add(dichVu1);
        }
    }

    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (DichVu dichVu : selectedDichVuList) {
            totalPrice += dichVu.getLichSuGiaDichVuList().get(0).getGia() * dichVu.getSoLuong();
        }
        textField.setText(String.valueOf(totalPrice));
    }

    private void handleFind(String tenDV) {
        List<DichVu> listDichVuByTen = dichVuDAO.getDSDichVuTheoTen(tenDV);
        loadDichVu(listDichVuByTen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnChange) {
            int selectedRow = tableOrderedServices.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ từ danh sách đã đặt.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            curTempQuantity = selectedDichVuList.get(selectedRow).getSoLuong();
            DichVu dichVu1 = selectedDichVuList.get(selectedRow);

            try {
                handleDeleteTask();
                onDichVuPanelClicked(dichVu1);
                updateOrderedServicesTable();
                //ỏ 7 đây cái hàm nào thay đổi db
                loadDichVu(listDichVu);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số nguyên không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else if (source == btnDelete) {
            handleDeleteTask();
        } else if (source == btnClearAll) {
            clearAllData();
        } else if (source == btnApply) {
            int key = JOptionPane.showConfirmDialog(this, "Bạn có muốn đặt dịch vụ?", "", JOptionPane.YES_NO_OPTION);
            if (key == JOptionPane.YES_OPTION) {
                setVisible(false);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy đặt dịch vụ!");
            }
        } else if (source == btnFind) {
            if (!(txtServiceName.getText().equalsIgnoreCase(""))) handleFind(txtServiceName.getText());
            else {
                loadDichVu(listDichVu);
            }
        }
    }

    private void handleDeleteTask() {
        int selectedRow = tableOrderedServices.getSelectedRow();

        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dịch vụ từ danh sách đã đặt.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        quantity = selectedDichVuList.get(selectedRow).getSoLuong();

        for (DichVu dichVu : listDichVu)
            if (selectedDichVuList.get(selectedRow).getMaDichVu().equalsIgnoreCase(dichVu.getMaDichVu())) {
                dichVu.setSoLuong(dichVu.getSoLuong() + quantity);
                dichVuDAO.updateDichVu(dichVu, dichVu.getMaDichVu());
            }

        selectedDichVuList.remove(selectedRow);
        insertChiTietDatDichVu();
        chiTietDatDichVuDAO.deleteChiTietDatDichVu(phieuDatPhongDAO.getPhieuDatPhongByMaHoaDon(hoaDon.getMaHoaDon()).get(0).getMaPhieuDatPhong());
        updateOrderedServicesTable();
        loadDichVu(listDichVu);

    }

    private boolean insertChiTietDatDichVu() {
        return chiTietDatDichVuDAO.insertChiTietDatDichVu(hoaDon.getPhieuDatPhongList().get(hoaDon.getPhieuDatPhongList().size() - 1).getMaPhieuDatPhong(), selectedDichVuList);
    }


    private void clearAllData() {
        for (DichVu dichVu : listDichVu)
            for (DichVu dichVu1 : selectedDichVuList)
                if (dichVu1.getMaDichVu().equalsIgnoreCase(dichVu.getMaDichVu())) {
                    dichVu.setSoLuong(dichVu.getSoLuong() + dichVu1.getSoLuong());
                    dichVuDAO.updateDichVu(dichVu, dichVu.getMaDichVu());
                }

        chiTietDatDichVuDAO.deleteChiTietDatDichVu(phieuDatPhongDAO.getPhieuDatPhongByMaHoaDon(hoaDon.getMaHoaDon()).get(0).getMaPhieuDatPhong());
        selectedDichVuList.clear();
        updateOrderedServicesTable();
        listDichVu = dichVuDAO.getAllDichVu();

        loadDichVu(listDichVu);
    }

}