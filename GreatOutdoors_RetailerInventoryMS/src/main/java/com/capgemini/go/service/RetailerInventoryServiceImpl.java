package com.capgemini.go.service;

import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.RollbackException;
import com.capgemini.go.exception.ExceptionConstants;
import com.capgemini.go.repository.RetailerInventoryRepository;
import com.capgemini.go.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.go.bean.RetailerInventoryBean;
import com.capgemini.go.dto.RetailerInventoryDTO;
import com.capgemini.go.dto.UserDTO;
import com.capgemini.go.exception.RetailerInventoryException;

import com.capgemini.go.utility.GoUtility;
@Service
public class RetailerInventoryServiceImpl implements RetailerInventoryService {
	

	
	@Autowired
	private RetailerInventoryRepository retailerInventoryRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<RetailerInventoryBean> getItemWiseDeliveryTimeReport(String retailerId) throws RetailerInventoryException {
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();
		List<RetailerInventoryDTO> listOfDeliveredItems = retailerInventoryRepository.findAllByretailerId(retailerId);		
		try {
			List<UserDTO> userList = (List<UserDTO>) userRepository.findAll();
			 for (RetailerInventoryDTO deliveredItem : listOfDeliveredItems) {
				RetailerInventoryBean object = new RetailerInventoryBean ();
			object.setRetailerId(retailerId);
				for (UserDTO user : userList) {
					if (user.getUserId().equals(retailerId)) {
						object.setRetailerName(user.getUserName());
					break;
				}
				}
				object.setRetailerName("vikash");
				object.setProductCategoryNumber(deliveredItem.getProductCategory());
				object.setProductCategoryName(GoUtility.getCategoryName(deliveredItem.getProductCategory()));
				object.setProductUniqueId(deliveredItem.getProductUniqueId());
				object.setDeliveryTimePeriod(GoUtility.calculatePeriod(deliveredItem.getProductDispatchTimestamp(), deliveredItem.getProductRecieveTimestamp()));
				object.setShelfTimePeriod(null);
				result.add(object);
			}		
		} catch (RuntimeException error) {
			throw new RetailerInventoryException ("getItemWiseDeliveryTimeReport - " + ExceptionConstants.INTERNAL_RUNTIME_ERROR);
		}
		return result;
	}

	@Override
	public List<RetailerInventoryBean> getCategoryWiseDeliveryTimeReport(String retailerId) throws RetailerInventoryException{
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();
		List<RetailerInventoryDTO> listOfDeliveredItems = retailerInventoryRepository.findAllByretailerId(retailerId); 
		Map<Integer, List<RetailerInventoryBean>> map = new HashMap<Integer, List<RetailerInventoryBean>>();
		for (int category = 1; category <= 5; category++)
			map.put(category, new ArrayList<RetailerInventoryBean>());	
		try {
			List<UserDTO> userList = (List<UserDTO>) userRepository.findAll();
			for (RetailerInventoryDTO deliveredItem : listOfDeliveredItems) {
				RetailerInventoryBean object = new RetailerInventoryBean ();
				object.setRetailerId(retailerId);
				for (UserDTO user : userList) {
					if (user.getUserId().equals(retailerId)) {
						object.setRetailerName(user.getUserName());
						break;
					}
				}
				object.setProductCategoryNumber(deliveredItem.getProductCategory());
				object.setProductCategoryName(GoUtility.getCategoryName(deliveredItem.getProductCategory()));
				object.setProductUniqueId(deliveredItem.getProductUniqueId());
				object.setDeliveryTimePeriod(GoUtility.calculatePeriod(deliveredItem.getProductDispatchTimestamp(), deliveredItem.getProductRecieveTimestamp()));
				object.setShelfTimePeriod(null);
				map.get(Integer.valueOf(object.getProductCategoryNumber())).add(object);
			}
			
			for (int category = 1; category <= 5; category++) {
				if (map.get(category).size() != 0) {
					int years = 0, months = 0, days = 0, count = 0;
					for (RetailerInventoryBean item : map.get(category)) {
						years += item.getDeliveryTimePeriod().getYears(); 
						months += item.getDeliveryTimePeriod().getMonths(); 
						days += item.getDeliveryTimePeriod().getDays();
						count ++;
					}
					years /= count;
					months /= count;
					days /= count;
					RetailerInventoryBean object = new RetailerInventoryBean ();
					object.setProductCategoryNumber((byte)category);
					object.setProductCategoryName(GoUtility.getCategoryName(category));
					object.setProductUniqueId("----");
					object.setDeliveryTimePeriod(Period.of(years, months, days));
					result.add(object);
				}
			}
			
		} catch (RuntimeException error) {
			error.printStackTrace();
			throw new RetailerInventoryException ("getCategoryWiseDeliveryTimeReport - " + ExceptionConstants.INTERNAL_RUNTIME_ERROR);
		}
		return result;
	}

	@Override
	public boolean updateProductRecieveTimeStamp(RetailerInventoryDTO retailerinventorydto) throws RetailerInventoryException {
		boolean receiveTimestampUpdated = false;

		try {

			RetailerInventoryDTO existingItem = (RetailerInventoryDTO) retailerInventoryRepository.findAll();
			if (existingItem == null) {
			throw new RetailerInventoryException(
						"updateProductReceiveTimeStamp - " + ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
			}
			existingItem.setProductRecieveTimestamp(retailerinventorydto.getProductRecieveTimestamp());
	
		} catch (IllegalStateException error) {
			throw new RetailerInventoryException(
					"updateProductReceiveTimeStamp - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			throw new RetailerInventoryException(
					"updateProductReceiveTimeStamp - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		} 
		receiveTimestampUpdated = true;
		return receiveTimestampUpdated;
		
	}
	
	@Override
	public boolean updateProductSaleTimeStamp(RetailerInventoryDTO retailerinventorydto) throws RetailerInventoryException {
		boolean saleTimestampUpdated = false;

		try {
			RetailerInventoryDTO existingItem = (RetailerInventoryDTO) retailerInventoryRepository.findAll();
			if (existingItem == null) {
				throw new RetailerInventoryException(
						"updateProductSaleTimeStamp - " + ExceptionConstants.PRODUCT_NOT_IN_INVENTORY);
			}
			existingItem.setProductSaleTimestamp(retailerinventorydto.getProductSaleTimestamp());
	
		} catch (IllegalStateException error) {
			throw new RetailerInventoryException(
					"updateProductSaleTimeStamp - " + ExceptionConstants.INAPPROPRIATE_METHOD_INVOCATION);
		} catch (RollbackException error) {
			throw new RetailerInventoryException(
					"updateProductSaleTimeStamp - " + ExceptionConstants.FAILURE_COMMIT_CHANGES);
		}
		saleTimestampUpdated = true;
		return saleTimestampUpdated;
		
	}
   
	@Override
	public List<RetailerInventoryDTO> getListOfRetailers() {
				return (List<RetailerInventoryDTO>) retailerInventoryRepository.findAll();
	}

	@Override
	public List<RetailerInventoryDTO> getInventoryById(String retailerId) {
		
		return retailerInventoryRepository.findAllByretailerId(retailerId);
	}
	
	public boolean deleteItemFromInventory(int retailerId, String productUIN) throws RetailerInventoryException {
	
		boolean itemDeleted = false;
		if(retailerInventoryRepository.findById(productUIN).isPresent())
		{
			retailerInventoryRepository.deleteById(productUIN);
			itemDeleted=true;
		}

		return itemDeleted;
	}
	
	public boolean addItemToInventory(String retailerId, byte productCategory, String productId, String productUIN) throws RetailerInventoryException {
		boolean itemAdded = false;
		Calendar currentSystemTimestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		RetailerInventoryDTO queryArgument = new RetailerInventoryDTO(retailerId, productCategory, productId, productUIN, currentSystemTimestamp, null, null);
		itemAdded = retailerInventoryRepository.save(queryArgument) != null;
		return itemAdded;
	}

	@Override
	public List<RetailerInventoryBean> getMonthlyShelfTimeReport(String retailerId, Calendar dateSelection)
			throws RetailerInventoryException {
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();

		List<RetailerInventoryDTO> listOfSoldItems =  retailerInventoryRepository.findAllByretailerId(retailerId);
		try {
			List<UserDTO> userList = (List<UserDTO>) userRepository.findAll();
			
			for (RetailerInventoryDTO soldItem : listOfSoldItems) {
				if (soldItem.getProductSaleTimestamp().get(Calendar.MONTH) == dateSelection.get(Calendar.MONTH)) {
					RetailerInventoryBean object = new RetailerInventoryBean ();
					object.setRetailerId(retailerId);
					object.setRetailerName("plawan");
					for (UserDTO user : userList) {
						if (user.getUserId().equals(retailerId)) {
							object.setRetailerName(user.getUserName());
							break;
						}
					}
					object.setProductCategoryNumber(soldItem.getProductCategory());
					object.setProductCategoryName(GoUtility.getCategoryName(soldItem.getProductCategory()));
					object.setProductUniqueId(soldItem.getProductUniqueId());
					object.setShelfTimePeriod(GoUtility.calculatePeriod(soldItem.getProductRecieveTimestamp(), 
							soldItem.getProductSaleTimestamp()));
					object.setDeliveryTimePeriod(null);
					result.add(object);
				} else {
					
				}
			}
		} catch (RuntimeException error) {
			//GoLog.getLogger(RetailerInventoryServiceImpl.class).error(error.getMessage());
			throw new RetailerInventoryException ("getMonthlyShelfTimeReport - " + ExceptionConstants.INTERNAL_RUNTIME_ERROR);
		}
		return result;
		 
	}

	@Override
	public List<RetailerInventoryBean> getQuarterlyShelfTimeReport(String retailerId, Calendar dateSelection)
			throws RetailerInventoryException {
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean> ();
		List<RetailerInventoryDTO> listOfSoldItems =  retailerInventoryRepository.findAllByretailerId(retailerId);
		try {
			List<UserDTO> userList = (List<UserDTO>) userRepository.findAll();
			
			for (RetailerInventoryDTO soldItem : listOfSoldItems) {
				RetailerInventoryBean object = new RetailerInventoryBean ();
				object.setRetailerId(retailerId);
				for (UserDTO user : userList) {
					if (user.getUserId().equals(retailerId)) {
						object.setRetailerName(user.getUserName());
						break;
					}
				}
				object.setProductCategoryNumber(soldItem.getProductCategory());
				object.setProductCategoryName(GoUtility.getCategoryName(soldItem.getProductCategory()));
				object.setProductUniqueId(soldItem.getProductUniqueId());
				object.setShelfTimePeriod(GoUtility.calculatePeriod(soldItem.getProductRecieveTimestamp(), 
						soldItem.getProductSaleTimestamp()));
				object.setDeliveryTimePeriod(null);
				result.add(object);
			}
			
	
		} catch (RuntimeException error) {
			//GoLog.getLogger(RetailerInventoryServiceImpl.class).error(error.getMessage());
			throw new RetailerInventoryException ("getQuarterlyShelfTimeReport - " + ExceptionConstants.INTERNAL_RUNTIME_ERROR);
		}
		return result;
	}

	@Override
	public List<RetailerInventoryBean> getYearlyShelfTimeReport(String retailerId, Calendar dateSelection)
			throws RetailerInventoryException {
		List<RetailerInventoryBean> result = new ArrayList<RetailerInventoryBean>();
		List<RetailerInventoryDTO> listOfSoldItems = retailerInventoryRepository.findAllByretailerId(retailerId);
		try {
			List<UserDTO> userList = (List<UserDTO>) userRepository.findAll();

			for (RetailerInventoryDTO soldItem : listOfSoldItems) {
				RetailerInventoryBean object = new RetailerInventoryBean();
				object.setRetailerId(retailerId);
				for (UserDTO user : userList) {
					if (user.getUserId().equals(retailerId)) {
						object.setRetailerName(user.getUserName());
						break;
					}
				}
				object.setProductCategoryNumber(soldItem.getProductCategory());
				object.setProductCategoryName(GoUtility.getCategoryName(soldItem.getProductCategory()));
				object.setProductUniqueId(soldItem.getProductUniqueId());
				object.setShelfTimePeriod(GoUtility.calculatePeriod(soldItem.getProductRecieveTimestamp(),
						soldItem.getProductSaleTimestamp()));
				object.setDeliveryTimePeriod(null);
				result.add(object);
			}

		} catch (RuntimeException error) {
			// logger.error(error.getMessage());
			throw new RetailerInventoryException(
					"getYearlyShelfTimeReport - " + ExceptionConstants.INTERNAL_RUNTIME_ERROR);
		}
		return result;
	}

}
