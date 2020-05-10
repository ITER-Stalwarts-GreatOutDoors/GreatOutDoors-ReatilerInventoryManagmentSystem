package com.capgemini.go.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.apache.log4j.Logger;

import com.capgemini.go.bean.RetailerInventoryBean;
import com.capgemini.go.exception.RetailerInventoryException;
import com.capgemini.go.service.RetailerInventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/RetailerInventory")
public class RetailerInventoryController {
	
	//private Logger logger = Logger.getRootLogger();
	
	@Autowired
	private RetailerInventoryService retailerInventoryService;

	public RetailerInventoryService getRetailerInventoryService() {
		return retailerInventoryService;
	}

	public void setRetailerInventoryService(RetailerInventoryService retailerInventoryService) {
		this.retailerInventoryService = retailerInventoryService;
	}
		
	
	
	 
	
	@ResponseBody
	@PostMapping("/RetailerList")
	public String getRetailerList (@ResponseBody) {
		//logger.info("getRetailerList - " + "Request for Retailer List Received");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataResponse = mapper.createObjectNode();
		JsonArray retailerList = new JsonArray();
		try {
			List<RetailerInventoryBean> result = this.retailerInventoryService.getListOfRetailers();
			for (RetailerInventoryBean item : result) {
				JsonObject retailerObj = new JsonObject();
				retailerObj.addProperty ("retailerId", item.getRetailerId());
				retailerObj.addProperty("retailerName", item.getRetailerName());
				retailerList.add(retailerObj);
			}
		} catch (Exception error) {
			//logger.error("getRetailerList - " + error.getMessage());
			((ObjectNode) dataResponse).put("Error", error.getMessage());
			return dataResponse.toString();
		}
		//logger.info("getRetailerList - " + "Sent requested data");
		return retailerList.toString();
	}
	
	@ResponseBody
	@GetMapping("/RetailerInventoryById/{retailerId}")
	public String getRetailerInventoryById (@PathVariable String retailerId) {
		logger.info("getRetailerInventoryById - " + "Request for " + retailerId + " Inventory Received");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataResponse = mapper.createObjectNode();
		JsonArray itemList = new JsonArray();
		try {
			List<RetailerInventoryBean> result = this.retailerInventoryService.getInventoryById(retailerId);
			for (RetailerInventoryBean item : result) {
				JsonObject itemObj = new JsonObject();
				itemObj.addProperty ("retailerId", item.getRetailerId());
				itemObj.addProperty("retailerName", item.getRetailerName());
				itemObj.addProperty("productCategoryNumber", item.getProductCategoryNumber());
				itemObj.addProperty("productCategoryName", item.getProductCategoryName());
				itemObj.addProperty("productName", item.getProductName());
				itemObj.addProperty("productUniqueId", item.getProductUniqueId());
				itemList.add(itemObj);
			}
		} catch (Exception error) {
			logger.error("getRetailerInventoryById - " + error.getMessage());
			((ObjectNode) dataResponse).put("Error", error.getMessage());
			return dataResponse.toString();
		}
		logger.info("getRetailerInventoryById - " + "Sent requested data");
		return itemList.toString();
	}
	
	 