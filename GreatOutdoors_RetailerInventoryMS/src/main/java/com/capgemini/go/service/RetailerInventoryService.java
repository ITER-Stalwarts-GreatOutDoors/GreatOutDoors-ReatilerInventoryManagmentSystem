package com.capgemini.go.service;

import java.util.Calendar;
import java.util.List;

import com.capgemini.go.bean.RetailerInventoryBean;
import com.capgemini.go.dto.RetailerInventoryDTO;
import com.capgemini.go.exception.RetailerInventoryException;

public interface RetailerInventoryService {
	public List<RetailerInventoryBean> getItemWiseDeliveryTimeReport(String retailerId) throws RetailerInventoryException;
	public List<RetailerInventoryBean> getCategoryWiseDeliveryTimeReport(String retailerId) throws RetailerInventoryException;
	public boolean updateProductRecieveTimeStamp(RetailerInventoryDTO retailerinventorydto) throws RetailerInventoryException;
	public boolean updateProductSaleTimeStamp(RetailerInventoryDTO retailerinventorydto) throws RetailerInventoryException;
	public List<RetailerInventoryDTO> getListOfRetailers();
	public List<RetailerInventoryDTO> getInventoryById(String retailerId);
	public List<RetailerInventoryBean> getMonthlyShelfTimeReport(String retailerId, Calendar dateSelection) throws RetailerInventoryException;;
	public List<RetailerInventoryBean> getQuarterlyShelfTimeReport(String retailerId, Calendar dateSelection) throws RetailerInventoryException;;
	public List<RetailerInventoryBean> getYearlyShelfTimeReport(String retailerId, Calendar dateSelection) throws RetailerInventoryException;;

}
