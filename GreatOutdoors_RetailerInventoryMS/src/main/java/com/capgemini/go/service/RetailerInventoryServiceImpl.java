package com.capgemini.go.service;

import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.persistence.RollbackException;

import com.capgemini.go.exception.ExceptionConstants;
import com.capgemini.go.exception.UserException;
import com.capgemini.go.repository.RetailerInventoryRepository;
import com.capgemini.go.repository.UserRepository;

import org.apache.catalina.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import com.capgemini.go.bean.RetailerInventoryBean;
import com.capgemini.go.dao.RetailerInventoryDao;
import com.capgemini.go.dto.RetailerInventoryDTO;
import com.capgemini.go.dto.UserDTO;
import com.capgemini.go.dao.UserDao;
import com.capgemini.go.exception.RetailerInventoryException;
import com.capgemini.go.utility.GoUtility;

@Service (value = "retailerInventoryService")
public class RetailerInventoryServiceImpl implements RetailerInventoryService {
	
	@Autowired
	private SessionFactory sessionFactory;
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	
	@Autowired
	private RetailerInventoryRepository retailerInventoryRepository;
	@Autowired
	private UserRepository userRepository;
	
	

	public List<RetailerInventoryBean> getListOfRetailers() throws RetailerInventoryException {
		//logger.info("getListOfRetailers - function called");
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();
		
		List<RetailerInventoryDTO> tempListOfDeliveredItems = (List<RetailerInventoryDTO>) retailerInventoryRepository.findAll();
				//this.retailerInventoryDao.getListOfRetailers();
		List<RetailerInventoryDTO> listOfDeliveredItems = new ArrayList<RetailerInventoryDTO> ();
		for (int index = 0; index < tempListOfDeliveredItems.size(); index++) {
			listOfDeliveredItems.add(new RetailerInventoryDTO (String.valueOf(tempListOfDeliveredItems.get(index)), 
					(byte)0, null, null, null, null, null));
		}
		//logger.info("getListOfRetailers - List extracted");
		
	
		try {
			List<UserDTO> userList = (List<UserDTO>)userRepository.findAll();
					//this.userDao.getUserIdList();
		} catch (UserException error) {
			//logger.info("getListOfRetailers - " + error.getMessage());
			throw new RetailerInventoryException ("getListOfRetailers - " + error.getMessage());
		}
		
		for (RetailerInventoryDTO item : listOfDeliveredItems) {
			String retailerName = null;
			for (UserDTO user : userList) {
				if (user.getUserId().equals(item.getRetailerId())) {
					retailerName = user.getUserName();
					break;
				}
			}
			RetailerInventoryBean object = new RetailerInventoryBean ();
			object.setRetailerId(item.getRetailerId());
			object.setRetailerName(retailerName);
			result.add(object);
		}
		//logger.info("getListOfRetailers - function return");
		return result;
	}

	/*******************************************************************************************************
	 * - Function Name : addItemToInventory <br>
	 * - Description : to add an item to inventory <br>
	 * - This function is to be called in the Order service when Order is placed by a retailer
	 * 
	 * @return boolean (true: if item added | false: otherwise)
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean addItemToInventory(String retailerId, byte productCategory, String productId, String productUIN) throws RetailerInventoryException {
		//logger.info("addItemToInventory - function called");
		boolean itemAdded = false;
		Calendar currentSystemTimestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		RetailerInventoryDTO queryArgument = new RetailerInventoryDTO(retailerId, productCategory, productId, productUIN, currentSystemTimestamp, null, null);
		itemAdded = this.retailerInventoryDao.insertItemInRetailerInventory(queryArgument);
		logger.info("addItemToInventory - function return");
		return itemAdded;
	}

	/*******************************************************************************************************
	 * - Function Name : deleteItemFromInventory <br>
	 * - Description : to delete an item from inventory <br>
	 * - This function is to be called if Order is canceled by a retailer
	 * 
	 * @return boolean (true: if item deleted | false: otherwise)
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public boolean deleteItemFromInventory(String retailerId, String productUIN) throws RetailerInventoryException {
		logger.info("deleteItemFromInventory - function called");
		RetailerInventoryDTO queryArgument = new RetailerInventoryDTO(retailerId, (byte)0, null, productUIN, null, null, null);
		boolean itemDeleted = this.retailerInventoryDao.deleteItemInRetailerInventory(queryArgument);
		logger.info("deleteItemFromInventory - function return");
		return itemDeleted;
	}

	

	/*******************************************************************************************************
	 * - Function Name : getInventoryById <br>
	 * - Description : to get inventory of a particular retailer <br>
	 * 
	 * @return List<RetailerInventoryBean>
	 * @throws RetailerInventoryException
	 *******************************************************************************************************/
	public List<RetailerInventoryBean> getInventoryById(String retailerId) throws RetailerInventoryException {
		logger.info("getInventoryById - function called with argument (" + retailerId + ")");
		RetailerInventoryDTO queryArgument = new RetailerInventoryDTO(retailerId, (byte)0, null, null, null, null, null);
		List<RetailerInventoryDTO> itemList = this.retailerInventoryDao.getItemListByRetailer(queryArgument);
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();
		
		try {
			Optional<RetailerInventoryBean> retailerDetails = userRepository.findById(retailerId);
					//this.userDao.getUserById(retailerId);
			
			List<ProductDTO> productList = this.productDao.viewAllProducts();
			
			for (RetailerInventoryDTO item : itemList) {
				RetailerInventoryBean itemBean = new RetailerInventoryBean ();
				itemBean.setRetailerId(retailerId);
				itemBean.setRetailerName(retailerDetails.getUserName());
				itemBean.setProductCategoryNumber(item.getProductCategory());
				itemBean.setProductCategoryName(GoUtility.getCategoryName(item.getProductCategory()));
				for (ProductDTO product : productList) {
					if (product.getProductId().equals(item.getProductId())) {
						itemBean.setProductName(product.getProductName());
						break;
					} else {
						continue;
					}
				}
				itemBean.setProductUniqueId(item.getProductUniqueId());
				itemBean.setShelfTimePeriod(null);
				itemBean.setDeliveryTimePeriod(null);
				result.add(itemBean);
			}
		} catch (UserException | ProductException error) {
			logger.info("getInventoryById - " + error.getMessage());
			throw new RetailerInventoryException ("getInventoryById - " + error.getMessage());
		} 
		return result;
	}
}

