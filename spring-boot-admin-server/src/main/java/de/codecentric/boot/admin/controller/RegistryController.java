/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.boot.admin.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.codecentric.boot.admin.model.Application;
import de.codecentric.boot.admin.registry.ApplicationRegistry;
import de.codecentric.boot.admin.registry.ApplicationRegistryConflictException;

/**
 * REST controller for controlling registration of managed applications.
 */
@RestController
public class RegistryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryController.class);

	private final ApplicationRegistry registry;

	public RegistryController(ApplicationRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Register an application within this admin application.
	 * 
	 * @param app The application infos.
	 * @return The registered application.
	 */
	@RequestMapping(value = "/api/applications", method = RequestMethod.POST)
	public ResponseEntity<Application> register(@RequestBody Application app) {
		LOGGER.debug("Register application {}", app.toString());
		try {
			Application registeredApp = registry.register(app);
			return new ResponseEntity<Application>(registeredApp, HttpStatus.CREATED);
		} catch (ApplicationRegistryConflictException ex) {
			return new ResponseEntity<Application>(HttpStatus.CONFLICT);
		}
	}

	/**
	 * Get a single application out of the registry.
	 * 
	 * @param id The application identifier.
	 * @return The registered application.
	 */
	@RequestMapping(value = "/api/application/{id}", method = RequestMethod.GET)
	public ResponseEntity<Application> get(@PathVariable String id) {
		LOGGER.debug("Deliver registered application with ID '{}'", id);
		Application application = registry.getApplication(id);
		if (application != null) {
			return new ResponseEntity<Application>(application, HttpStatus.OK);
		} else {
			return new ResponseEntity<Application>(application, HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Unregister an application within this admin application.
	 * 
	 * @param id The application id.
	 */
	@RequestMapping(value = "/api/application/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Application> unregister(@PathVariable String id) {
		LOGGER.debug("Unregister application with ID '{}'", id);
		Application app = registry.unregister(id);
		if (app != null) {
			return new ResponseEntity<Application>(app, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Application>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * List all registered applications with name
	 * 
	 * @return List.
	 */
	@RequestMapping(value = "/api/applications", method = RequestMethod.GET)
	public Collection<Application> applications(@RequestParam(value = "name", required = false) String name) {
		LOGGER.debug("Deliver registered applications with name= {}", name);
		if (name == null || name.isEmpty()) {
			return registry.getApplications();
		} else {
			return registry.getApplicationsByName(name);
		}
	}
}
