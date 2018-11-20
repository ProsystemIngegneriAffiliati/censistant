INSERT INTO group_app(group_name) VALUES('user')
INSERT INTO group_app(group_name) VALUES('admin')

INSERT INTO user_app(user_name, pass_word, version) VALUES('maina', 'PuYC2SlAA74yPJE0eporJcAYhJfayYF+eqstExAB7ws=', 0)
INSERT INTO user_app(user_name, pass_word, version) VALUES('guest', 'hJg8YPfarcHLhphiH4AsDZ+aPDwpXIEHSPsEgRXBhuw=', 0)
INSERT INTO user_app(user_name, pass_word, version) VALUES('demoAdmin', 'KpdRbDVLaISM29j1SiJqClWyHtE44getbFy7nACqWuo=', 0)

INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('maina', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('maina', 'admin')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('guest', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('demoAdmin', 'user')
INSERT INTO users_groups_app(user_name, groups_group_name) VALUES('demoAdmin', 'admin')

INSERT INTO provenance(name, version) VALUES('Altro', 0)
INSERT INTO provenance(name, version) VALUES('CISL', 0)
INSERT INTO provenance(name, version) VALUES('Internet', 0)
INSERT INTO provenance(name, version) VALUES('Nostro cliente diretto', 0)
INSERT INTO provenance(name, version) VALUES('Nostro cliente indiretto', 0)
INSERT INTO provenance(name, version) VALUES('Pagine gialle', 0)
INSERT INTO provenance(name, version) VALUES('Sito CSA', 0)
INSERT INTO provenance(name, version) VALUES('Sito pagine gialle', 0)
INSERT INTO provenance(name, version) VALUES('Stampa', 0)
INSERT INTO provenance(name, version) VALUES('Ufficio', 0)
INSERT INTO provenance(name, version) VALUES('Volantino', 0)
INSERT INTO provenance(name, version) VALUES('Informatore', 0)

INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, taxcode, vatregistrationnumber, notes, provenance_id, version) VALUES('Prosystem Ingegneri Affiliati di Gilli Enrico & C.', 'Prosystem', false, false, false, true, false, '10805010013', '10805010013', 'Cliente o fornitore di prova', 4, 0)
INSERT INTO plant(customersupplier_id, isheadoffice, name, address, phone, fax, email, version) VALUES(1, true, 'Sede Prosystem', 'Via Nazionale, 24 10064 Pinerolo (TO)', '0123 456789', '0123 567890', 'info@prosystemingegneri.com', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Gilli Enrico', null, '3291234567', 'gilli@prosystemingegneri.com', 'Titolare', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Mainardi Davide', null, '3461234567', 'mainardi@prosystemingegneri.com', null, 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Carioti Ilaria', null, '3931234567', 'carioti@prosystemingegneri.com', 'In maternità fino a gennaio 2018', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, notes, version) VALUES(1, 'Elena', '0121202147', null, 'info@prosystemingegneri.com', 'Segretaria part-time al mattino', 0)

INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, provenance_id, version) VALUES('Ferrero cioccolatini SpA', 'Ferrero', false, false, true, true, false, 4, 0)
INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, provenance_id, version) VALUES('Santavicca Costruzioni', 'Santavicca', false, false, true, true, false, 3, 0)
INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, provenance_id, version) VALUES('Mario Rossi', 'Mario Rossi', false, false, true, true, false, 2, 0)

INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, provenance_id, version) VALUES('FCA Motors', 'FIAT', false, false, false, true, true, 6, 0)
INSERT INTO plant(customersupplier_id, isheadoffice, name, address, phone, fax, email, version) VALUES(5, true, 'Sede', 'Corso Agnelli, 1 10100 Torino (TO)', '011 456789', '011 567890', 'info@fca.com', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, version) VALUES(5, 'Marchionne', null, '3391234567', 'marchionne@fca.com', 0)
INSERT INTO location(dtype, version) VALUES(1, 0)
INSERT INTO supplierplantlocation(id, plant_id) VALUES(1, 2)

INSERT INTO customersupplier(businessname, name, isofferaccepted, isonlyinfo, ispotentialcustomer, iscustomer, issupplier, version) VALUES('Apple Inc', 'Apple', false, false, false, false, true, 0)
INSERT INTO plant(customersupplier_id, isheadoffice, name, address, phone, fax, email, version) VALUES(6, true, 'Sede', 'Cupertino (USA)', '011 123456', '011 654321', 'info@apple.com', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, version) VALUES(6, 'Steve Jobs', null, '3381234567', 'jobs@apple.com', 0)
INSERT INTO referee(customersupplier_id, name, phone, mobile, email, version) VALUES(6, 'Tim Cook', null, '3371234567', 'cook@apple.com', 0)
INSERT INTO location(dtype, version) VALUES(1, 0)
INSERT INTO supplierplantlocation(id, plant_id) VALUES(2, 3)

INSERT INTO worker(userapp_user_name, name, initials, email, version) VALUES('maina', 'Mainardi Davide', 'MD', 'mainardi@prosystemingegneri.com', 0)

INSERT INTO systemtype(name, symbol, version) VALUES('Anti - Intrusione', 'A', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Video - TVCC', 'V', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Rivelazione Incendio', 'RI', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Controllo Accessi', 'CA', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Altro', 'oth', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('We.R', 'WR', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Fornitura materiale antintrusione', 'FMA', 0)
INSERT INTO systemtype(name, symbol, version) VALUES('Fornitura materiale TVCC', 'FMV', 0)

INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('14', '2016-12-30', 1, 1, true, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('13', '2016-12-28', 4, 1, false, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('1', '2017-01-08', 5, 3, true, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('2', '2017-01-10', 4, 2, true, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('3', '2017-02-01', 1, 3, false, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('11', '2016-06-08', 5, 3, false, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('12', '2016-07-10', 4, 5, true, 0)
INSERT INTO sitesurveyrequest(number, creation, customer_id, systemType_id, isinfo, version) VALUES('10', '2016-05-01', 1, 4, false, 0)

INSERT INTO sitesurveyreport(number, expected, actual, plant_id, seller_id, request_id, isofferaccepted, version) VALUES('1', '2017-01-10', '2017-01-10', 2, 1, 3, false, 0)
INSERT INTO sitesurveyreport(number, expected, actual, plant_id, seller_id, request_id, isofferaccepted, version) VALUES('245', '2016-12-15', null, 1, 1, 1, false, 0)

INSERT INTO unitmeasure(name, symbol, version) VALUES('Metri', 'm', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Pezzi', 'pz.', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Numero', 'nr.', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Scatola', 'scatola', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Matassa', 'matassa', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Chilogrammi', 'kg', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Litri', 'l', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Metri quadri', 'm²', 0)
INSERT INTO unitmeasure(name, symbol, version) VALUES('Metri cubi', 'm³', 0)

INSERT INTO item(description, unitmeasure_id, version) VALUES('Sensore', 2, 0)
INSERT INTO item(description, unitmeasure_id, version) VALUES('Cavo', 1, 0)
INSERT INTO item(description, unitmeasure_id, version) VALUES('Smartphone', 2, 0)
INSERT INTO item(description, unitmeasure_id, version) VALUES('Automobile', 2, 0)

INSERT INTO box(quantity, unitmeasure_id, version) VALUES(1, 4, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(5, 4, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(10, 4, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(12, 2, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(6, 2, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(100, 5, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(50, 5, 0)
INSERT INTO box(quantity, unitmeasure_id, version) VALUES(25, 5, 0)

INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('123x', 'iPhone X', 3, 6, 0)
INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('1238', 'iPhone 8', 3, 6, 0)
INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('1238p', 'iPhone 8 plus', 3, 6, 0)
INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('cinquec', '500', 4, 5, 0)
INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('cinquecics', '500 X', 4, 5, 0)
INSERT INTO supplieritem(code, description, item_id, supplier_id, version) VALUES('cinquecelle', '500 L', 4, 5, 0)

INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 1, 1000, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(3, 1, 999, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 2, 800, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(3, 2, 798, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 3, 850, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(2, 3, 850, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(3, 3, 845, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 4, 15000, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 5, 25000, 0)
INSERT INTO boxeditem(box_id, item_id, cost, version) VALUES(1, 6, 20000, 0)

INSERT INTO purchaseorder(plant_id, creation, number, version) VALUES(3, '2017-10-01', 1, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (1, 1, 950, 7, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (1, 2, 9985, 1, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (1, 3, 800, 3, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (1, 6, 4250, 7, 0)

INSERT INTO purchaseorder(plant_id, creation, number, version) VALUES(2, '2017-11-01', 2, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (2, 8, 14999, 2, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (2, 9, 24999, 2, 0)
INSERT INTO purchaseorderrow(purchaseorder_id, boxeditem_id, cost, quantity, version) VALUES (2, 10, 20500, 10, 0)

INSERT INTO location(dtype, version) VALUES(0, 0)
INSERT INTO warehouse(id, name, description) VALUES(3, 'Magazzino principale CSA', 'Magazzino centrale')
INSERT INTO location(dtype, version) VALUES(0, 0)
INSERT INTO warehouse(id, name, description) VALUES(4, 'Magazzino secondario CSA', null)

INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(1, 1, '2017-10-30 08:00:00', 1, 2, 3, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(3, 1, '2017-10-30 08:00:01', 3, 2, 3, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(3, 1, '2017-10-30 08:30:00', 2, 3, 4, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(3, 1, '2017-10-30 08:30:30', 1, 3, 2, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(6, 1, '2017-11-01 08:30:30', 2, 1, 3, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(6, 1, '2017-11-02 08:30:30', 1, 3, 1, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(9, 1, '2017-11-02 08:35:30', 1, 3, 4, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(9, 1, '2017-11-03 08:30:30', 10, 1, 4, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(9, 1, '2017-11-03 08:30:30', 5, 4, 3, 0)
INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(2, 1, '2017-11-04 08:00:00', 3, 2, 4, 0)

INSERT INTO carrier(name, version) VALUES('TNT', 0)
INSERT INTO carrier(name, version) VALUES('SDA', 0)
INSERT INTO carrier(name, version) VALUES('BRT', 0)
INSERT INTO carrier(name, version) VALUES('Express Courier', 0)

INSERT INTO goodsdescription(name, version) VALUES('Cassoni', 0)
INSERT INTO goodsdescription(name, version) VALUES('Scatole', 0)
INSERT INTO goodsdescription(name, version) VALUES('Sfuso', 0)
INSERT INTO goodsdescription(name, version) VALUES('Vassoi', 0)
INSERT INTO goodsdescription(name, version) VALUES('Bobine', 0)
INSERT INTO goodsdescription(name, version) VALUES('A vista', 0)

INSERT INTO shipmentreason(name, version) VALUES('Vendita', 0)
INSERT INTO shipmentreason(name, version) VALUES('Conto lavoro', 0)
INSERT INTO shipmentreason(name, version) VALUES('Riparazione', 0)
INSERT INTO shipmentreason(name, version) VALUES('Campionatura', 0)
INSERT INTO shipmentreason(name, version) VALUES('Esposizione', 0)
INSERT INTO shipmentreason(name, version) VALUES('Conto visione', 0)

INSERT INTO shippingpayment(name, version) VALUES('Porto franco', 0)
INSERT INTO shippingpayment(name, version) VALUES('Porto assegnato', 0)

INSERT INTO deviceprogrammingtype(name, version) VALUES('Immediato', 0)
INSERT INTO deviceprogrammingtype(name, version) VALUES('Ritardato', 0)
INSERT INTO deviceprogrammingtype(name, version) VALUES('24h', 0)

INSERT INTO placetype(name, version) VALUES('Industriale', 0)
INSERT INTO placetype(name, version) VALUES('Abitazione privata', 0)
INSERT INTO placetype(name, version) VALUES('Attività commerciale', 0)
INSERT INTO placetype(name, version) VALUES('Altro', 0)

INSERT INTO location(dtype, version) VALUES(2, 0)
INSERT INTO system(id, description) VALUES(5, 'Impianto di prova')

INSERT INTO device(system_id, item_id, quantity, devices_order, version) VALUES(5, 3, 10, 0, 0)
INSERT INTO device(system_id, item_id, quantity, devices_order, version) VALUES(5, 4, 3, 1, 0)

INSERT INTO offer(creation, number, sitesurveyreport_id, system_id, version) VALUES('2017-12-20', 1, 1, 5, 0)

INSERT INTO joborder(creation, number, placetype_id, offer_id, version) VALUES('2017-12-21', 1, 2, 1, 0)

INSERT INTO handleditem(boxeditem_id, worker_id, handlingtimestamp, quantity, fromlocation_id, tolocation_id, version) VALUES(1, 1, '2017-12-20 11:14:00', 1, 3, 5, 0)

INSERT INTO preventivemaintenance(name, version) VALUES('Manutezione preventiva', 0)
INSERT INTO preventivemaintenance(name, version) VALUES('Telecontrollo', 0)

INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Alimentazione 220V', 0, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Alimentazione 12V', 1, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Accumulatori', 2, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Prova sensori', 3, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Dispositivi di allarme', 4, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (1, 'Collaudo sistema', 5, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (2, 'Connessione all''impianto', 0, 0)
INSERT INTO inspection(preventivemaintenance_id, name, inspections_order, version) VALUES (2, 'Verifica parametri', 1, 0)

INSERT INTO maintenancepayment(name, version) VALUES('Non fatturare - garanzia', 0)
INSERT INTO maintenancepayment(name, version) VALUES('Inviare fattura', 0)
INSERT INTO maintenancepayment(name, version) VALUES('Pagamento contante', 0)
INSERT INTO maintenancepayment(name, version) VALUES('Pagamento con assegno', 0)
INSERT INTO maintenancepayment(name, version) VALUES('Non effettuato', 0)

INSERT INTO maintenancecontract(creation, isfullservice, isoncall, version) VALUES('2018-08-18', true, false, 0)
INSERT INTO maintenancecontract_system(maintenancecontract_id, systems_id) VALUES(1, 5)
INSERT INTO scheduledmaintenance(maintenancecontract_id, preventivemaintenance_id, quantity, version) VALUES(1, 1, 1, 0)
INSERT INTO maintenancetask(closed, closingnotes, creation, customersignature, description, expiry, isguaranteevalid, issuitableforoperation, paymentnotes, inchargeworker_id, maintenancecontract_id, maintenanceworker_id, preventivemaintenance_id, system_id, version) VALUES('2018-08-29 00:00:00.000', 'Tutto fatto', '2018-08-18 00:00:00.000', '{"lines":[[[244,144.5],[238,128.5],[234,120.5],[232,103.5],[232,92.5],[237,82.5],[256,80.5],[291,84.5],[342,112.5],[363,141.5],[377,163.5],[386,174.5],[398,179.5],[418,182.5],[446,181.5],[484,169.5],[528,147.5],[574,121.5],[615,95.5],[633,79.5],[635,77.5],[631,76.5],[598,76.5],[528,83.5],[454,85.5],[314,83.5],[311,83.5],[329,90.5],[359,112.5],[397,134.5],[437,148.5],[479,158.5],[518,164.5],[552,166.5],[587,166.5],[617,162.5],[641,155.5],[651,152.5]]]}', 'Manutezione preventiva', '2019-08-18', false, true, null, 1, 1, 1, 1, 5, 0)
INSERT INTO taskprice(fixedcallamount, kilometerstravelled, priceperkilometer, travelexpenses, maintenancetask_id, normalworking_id, overtimeworking_id, travelworking_id, version) VALUES(0.00, 0, 0.00, 0.00, 1, null, null, null, 0)
INSERT INTO maintenancetask_maintenancepayment(maintenancetask_id, maintenancepayments_id) VALUES(1, 2)
INSERT INTO maintenancetask_maintenancepayment(maintenancetask_id, maintenancepayments_id) VALUES(1, 5)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(1, 1, 1, 0)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(1, 2, 1, 0)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(1, 3, 1, 0)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(2, 4, 1, 0)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(1, 5, 1, 0)
INSERT INTO inspectiondone(inspectionresult, inspection_id, maintenancetask_id, version) VALUES(1, 6, 1, 0)
