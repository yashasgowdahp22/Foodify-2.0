package com.foodify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodify.model.MenuItem;
import com.foodify.service.MenuItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Menu Management", description = "Menu item APIs")
@RestController
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;

	@PostMapping("addMenuItem")
	public void addMenuItem(@RequestBody MenuItem menuItem) {
		menuItemService.addMenuItem(menuItem);
	}

	@Operation(
			summary = "Get all menu items",
			description = "Retrieves all available menu items from restaurants"
			)
	@GetMapping("getAllMenuItems")
	public List<MenuItem> getAllMenuItem() {
		return menuItemService.getAllMenuItem();
	}

	@GetMapping("getMenuItem/{Id}")
	public MenuItem getMenuItem(@PathVariable Long Id) {
		return menuItemService.getMenuItem(Id);
	}

	@PutMapping("updateMenuItem/{Id}")
	public String updateMenuItem(@RequestBody MenuItem menuItem, @PathVariable Long Id) {
		return menuItemService.updateMenuItem(menuItem, Id);
	}

	@DeleteMapping("deleteMenuItem/{Id}")
	public void deleteMenuItem(@PathVariable Long Id) {
		menuItemService.deleteMenuItem(Id);
	}
}