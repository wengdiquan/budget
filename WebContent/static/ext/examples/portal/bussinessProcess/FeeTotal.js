// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('App.bussinessProcess.FeeTotal.InfoWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '模板保存',
				width : 420,
				height : 350,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'left',
						labelWidth : 90,
						anchor : '100%'
					},
					items : [{
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					},{
						xtype : 'textfield',
						name : 'name',
						fieldLabel:'模板名称<font color="red">*</font>',
						allowBlank : false
					}],
					buttons : [ '->', {
						id : 'infowindow-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							if (form.isValid()) {
								window.getEl().mask('数据保存中，请稍候...');
								var vals = form.getValues();
								var store = Ext.getCmp('feetotal-feedetailgrid').getStore();
								var paramsA = [];
								store.each(function(record){
									var node = record.data;
									paramsA.push(node);
								});
								var paramsfinal = {
										name : vals.name,
										Temvo : Ext.encode(paramsA)
								};
								Ext.Ajax.request({
									url : appBaseUri + '/feetotal/saveOrUpdateValue',
									params : paramsfinal ,
									//headers: {'Content-Type':'application/json'},
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('feetotal-feedetailgrid').getStore().reload();
											} else {
												globalObject.errTip(res.msg);
											}
										}
									},
									failure : function(response) {
										globalObject.errTip('操作失败！');
									}
								});
								window.getEl().unmask();
								window.close();
							}
						}
					}, {
						id : 'infowindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					},'->']
				} ]
			});
			App.bussinessProcess.FeeTotal.InfoWindow.superclass.constructor.call(this, config);
		}
	});
	//模板选择
	Ext.define('App.bussinessProcess.FeeTotal.InfoWindowSelect', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '模板选择',
				id : 'mywin',
				width : 420,
				height : 350,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'left',
						labelWidth : 90,
						anchor : '100%'
					},
					items : [{
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					},
					Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeTemplateGrid')
					],
					buttons : [ '->'/*, {
						id : 'infowindow-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							if (form.isValid()) {
								window.getEl().mask('数据保存中，请稍候...');
								var vals = form.getValues();
								
								var grid = Ext.getCmp("categoryandmodelmanage-YCAGrid");
								var records = grid.getSelectionModel().getSelection();
								vals.lookValueId = records[0].get('lookValueId')-0 ;
								
								Ext.Ajax.request({
									url : appBaseUri + '/categorymodel/saveOrUpdateValue',
									params : vals,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('categoryandmodelmanage-cmdetailgrid').getStore().reload();
												Ext.getCmp('categoryandmodelmanage-YCAGrid').getStore().reload();
											} else {
												globalObject.errTip(res.msg);
											}
										}
									},
									failure : function(response) {
										globalObject.errTip('操作失败！');
									}
								});
								window.getEl().unmask();
								window.close();
							}
						}
					}*/, {
						id : 'infowindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					},'->']
				} ]
			});
			App.bussinessProcess.FeeTotal.InfoWindowSelect.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeTemplateGrid', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feetemplategrid',
		plain : true,
		region : 'center',
		height : 250,
		border:true,
		initComponent : function() {
			var me = this;
			Ext.define('FeeTemplateList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'int'
				},  'name']
			});
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'FeeTemplateList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/feetotal/queryFeeTemplate',
					extraParams : {"templetId": 1},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ 
			{
				text : "模板名称",
				dataIndex : 'name',
				sortable : false,
				width : '40%'
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			}];
			Ext.apply(this, {
				store : costValueStore,
				/*listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						me.feeTotalId = record.data.id;
					}
				},*/
				selModel : Ext.create('Ext.selection.Model'),//Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		},
		listeners : {
			'itemdblclick' : function(_this, record, td, cellIndex, tr, rowIndex, e, eOpts){
		        	var store = Ext.getCmp('feetotal-feedetailgrid').getStore();  
		        	Ext.apply(store.proxy.extraParams, {"templetId": record.data.id});
		        	Ext.getCmp('feetotal-feedetailgrid').getStore().reload();
		        	Ext.getCmp('mywin').close();
			}
		}
	});
	
	// 费用汇总
	Ext.define('Budget.app.bussinessProcess.FeeTotal', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
						            	  Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeDetailGrid'),
						            	  {	
								              region:'center', 
								              id: 'center-region-container11', 
								              layout: 'border', 
								              items : [
								            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeTypeGrid'),
								            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeValueGrid')
								                       ]
								         }
				    ]
			});
			this.callParent(arguments);
		}
	});
	
	// 详细值
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeDetailGrid', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feedetailgrid',
		plain : true,
		region : 'north',
		height : '60%',
		border:true,
		plugins:[{
  	        ptype: 'cellediting',
  	        clicksToEdit: 1,
  	        editing:false, 
  	        listeners:{
  	        	beforeedit:function(editor , context , eOpts){
  	        		var records = editor.grid.getSelectionModel().getSelection();
  	        		if(records[0].data.id == undefined){
  	        			return false;
  	        		}
  	        	}
  	        }
  	    }],
		initComponent : function() {
			var me = this;
			Ext.define('FeeValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id_X',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'seq', 'feeCode', 'name', 'calculatedRadix','radixRemark','rate','amount', 'remark', 'templetId']
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'FeeValueList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/feetotal/queryFeeTotalInfo',
					extraParams : {"templetId": 1},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "序号",
				dataIndex : 'seq',
				width : '7%',
				hidden : false,
				editor:{
			        xtype: 'textfield',
			        allowDecimals: true,
			        decimalPrecision: 5,
			        listeners :{
			        	'change': function(obj, newValue, oldValue, eOpt){
			        		var grid = Ext.getCmp("feetotal-feedetailgrid");
			    			var bitRecord = grid.getSelectionModel().getSelection()[0];
			        		Ext.Ajax.request({
								 url : appBaseUri + '/feetotal/updateValue', 
								 params : {
									 id :bitRecord.get('id'),
									 seq :newValue
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 me.reloadAndSelect();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
			        		
			        	}
			        }
			    }
			},{
				text : "费用代号",
				dataIndex : 'feeCode',
				sortable : false,
				width : '7%',
				editor:{
			        xtype: 'textfield',
			        allowDecimals: true,
			        decimalPrecision: 5,
			        listeners :{
			        	'change': function(obj, newValue, oldValue, eOpt){
			        		var grid = Ext.getCmp("feetotal-feedetailgrid");
			    			var bitRecord = grid.getSelectionModel().getSelection()[0];
			        		Ext.Ajax.request({
								 url : appBaseUri + '/feetotal/updateValue', 
								 params : {
									 id :bitRecord.get('id'),
									 feeCode :newValue
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 me.reloadAndSelect();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
			        		
			        	}
			        }
			    }
			}, {
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%',
				editor:{
			        xtype: 'textfield',
			        allowDecimals: true,
			        decimalPrecision: 5,
			        listeners :{
			        	'change': function(obj, newValue, oldValue, eOpt){
			        		var grid = Ext.getCmp("feetotal-feedetailgrid");
			    			var bitRecord = grid.getSelectionModel().getSelection()[0];
			        		Ext.Ajax.request({
								 url : appBaseUri + '/feetotal/updateValue', 
								 params : {
									 id :bitRecord.get('id'),
									 name :newValue
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 me.reloadAndSelect();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
			        		
			        	}
			        }
			    }
			},{
				text : "计算基数",
				dataIndex : 'calculatedRadix',
				sortable : false,
				width : '20%',
				editor: {
					xtype:'triggerfield',
					triggerCls: Ext.baseCSSPrefix + 'form-search-trigger',
					editable:false,
					onTriggerClick:function(){
						Ext.create("Budget.app.bussinessProcess.FeeTotalCalculatedWin").show();
					}
				}
			},{
				text : "基数说明",
				dataIndex : 'radixRemark',
				sortable : false,
				width : '20%'
			},{
				text : "费率(%)",
				dataIndex : 'rate',
				sortable : false,
				width : '10%',
				editor:{
			        xtype: 'textfield',
			        allowDecimals: true,
			        decimalPrecision: 5,
			        listeners :{
			        	'change': function(obj, newValue, oldValue, eOpt){
			        		var grid = Ext.getCmp("feetotal-feedetailgrid");
			    			var bitRecord = grid.getSelectionModel().getSelection()[0];
			        		Ext.Ajax.request({
								 url : appBaseUri + '/feetotal/updateValue', 
								 params : {
									 id :bitRecord.get('id'),
									 rate :newValue
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 me.reloadAndSelect();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
			        		
			        	}
			        }
			    }
			},{
				text : "金额",
				dataIndex : 'amount',
				sortable : false,
				width : '10%'
			},{
				text : "备注",
				dataIndex : 'remark',
				sortable : false,
				width : '10%',
				editor:{
			        xtype: 'textfield',
			        allowDecimals: true,
			        decimalPrecision: 5,
			        listeners :{
			        	'change': function(obj, newValue, oldValue, eOpt){
			        		var grid = Ext.getCmp("feetotal-feedetailgrid");
			    			var bitRecord = grid.getSelectionModel().getSelection()[0];
			        		Ext.Ajax.request({
								 url : appBaseUri + '/feetotal/updateValue', 
								 params : {
									 id :bitRecord.get('id'),
									 remark :newValue
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 me.reloadAndSelect();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
			        		
			        	}
			        }
			    }
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "templetId",
				dataIndex : 'templetId',
				hidden : true
			}];
			Ext.apply(this, {
				store : costValueStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						me.feeTotalId = record.data.id;
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '保存模板',
					scope : this,
					handler : me.newCodeCodeFun
				},{
					xtype : 'button',
					iconCls : 'icon-edit',
					text : '载入模板',
					scope : this,
					handler : me.selectTempalteFun
				}, {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增一条记录',
					scope : this,
					handler : me.newItemFun
				}],
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
		/*	costValueStore.on('beforeload',function(){
				Ext.apply(costValueStore.proxy.extraParams, {"parentId": Ext.getCmp('categoryandmodelmanage-cmgrid').parentId});
			});*/
			
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var grid = Ext.getCmp('feetotal-feedetailgrid');
			var store = grid.getStore();
			if (store.getCount() <= 0 ) {
				globalObject.infoTip('请至少输入一条记录！');
				return;
			};
			var win = new App.bussinessProcess.FeeTotal.InfoWindow({
				hidden : true
			});
			var form = win.down('form').getForm();
			win.show();
			
		},
		newItemFun:function(){
			var grid = Ext.getCmp("feetotal-feedetailgrid");
			var records = grid.getSelectionModel().getSelection();
			var store = grid.getStore();
			var store11 = Ext.getCmp('feetotal-feedetailgrid').getStore();
			var templetId = "";
			store11.each(function(record){
				templetId = record.data.templetId;
			});
			//新增一条空白的
			Ext.getCmp('feetotal-feedetailgrid').getEl().mask('数据处理中，请稍候...');
			Ext.Ajax.request({
				 url : appBaseUri + '/feetotal/saveValue', //新增单位工程
				 params : {
					 templetId: templetId
				 },
				 method : "POST",
				 success : function(response) {
					 if (response.responseText != '') {
						 var res = Ext.JSON.decode(response.responseText);
						 if (res.success) {
							 store.reload();
						 } else {
							 globalObject.errTip(res.msg);
						 }
						 
						 Ext.getCmp('feetotal-feedetailgrid').getEl().unmask();
					 }
				 },
				 failure : function(response) {
					 globalObject.errTip('操作失败！');
				 }
			 });
		},
		selectTempalteFun: function(){
			var me = this;
			var win = new App.bussinessProcess.FeeTotal.InfoWindowSelect({
				hidden : true
			});
			var form = win.down('form').getForm();
			win.show();
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeTypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'feetotal-feetypegrid',
		region : 'west',
		width : '18%',
		border : true,
		title:"费用名称",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/lookvalue/queryLookTypeInfo',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : costTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						me.lookTypeId = record.get('id');
						var tmp = me.lookTypeId;
						if(record.get('id') == 0){
							tmp = "";
						}
						Ext.getCmp('feetotal-feevaluegrid').getStore().load({
							params : {
								'lookTypeId' : tmp
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feevaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('TotalYCAList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'type', 'name', 'unit', 'tax_Price', 'notax_Price', 'amount', 'tax_Single_SumPrice', 'single_SumPrice'  ]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'TotalYCAList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/ycatotal/queryYCATotalInfo',
					extraParams : {"lookTypeId": Ext.getCmp('feetotal-feetypegrid').lookTypeId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			}
			,{
				text : "编码",
				dataIndex : 'code',
				sortable : false,
				width : '15%'
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '15%'
			},{
				text : "含税合价",
				dataIndex : 'tax_Single_SumPrice',
				sortable : false,
				width : '12%'
			},{
				text : "不含税合价",
				dataIndex : 'single_SumPrice',
				sortable : false,
				width : '12%'
			}];
			Ext.apply(this, {
				store : costValueStore,
				selModel : Ext.create('Ext.selection.Model'),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		},
		listeners : {
			'itemdblclick' : function(_this, record, td, cellIndex, tr, rowIndex, e, eOpts){
				var data = Ext.getCmp('feetotal-feedetailgrid').getSelectionModel().getSelection();  
				if (data.length == 0) {
					globalObject.infoTip('请先选择一条费用汇总记录！');
					return;
				} else {  
					var calculatedRadix = "";
					if(data[0].get("calculatedRadix") == null || data[0].get("calculatedRadix") == ""){
						calculatedRadix = record.data.code;
					}else{
						calculatedRadix = data[0].get("calculatedRadix") + "+" + record.data.code;
					}
					var radixRemark = "";
					if(data[0].get("radixRemark") == null || data[0].get("radixRemark") == ""){
						radixRemark = record.data.name;
					}else{
						radixRemark = data[0].get("radixRemark") + "+" + record.data.name;
					}
                    var vals = {
                    		id : data[0].get("id") ,
                    		calculatedRadix :  calculatedRadix,
                    		radixRemark : radixRemark,
                    		amount : data[0].get("amount") + record.data.tax_Single_SumPrice,
                    		cmd : "edit"
                    };
                    Ext.Ajax.request({
						url : appBaseUri + '/feetotal/updateValue',
						params : vals,
						method : "POST",
						success : function(response) {
							if (response.responseText != '') {
								var res = Ext.JSON.decode(response.responseText);
								if (res.success) {
									globalObject.msgTip('操作成功！');
									//Ext.getCmp('feetotal-feedetailgrid').getStore().reload();
									Ext.getCmp('feetotal-feedetailgrid').getStore().loadPage(1);
								} else {
									globalObject.errTip(res.msg);
								}
							}
						},
						failure : function(response) {
							globalObject.errTip('操作失败！');
						}
					});
		        }  
			}
		},
		newCodeCodeFun: function(){
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			})
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			
			var grid = Ext.getCmp("feetotal-feevaluegrid");
			var records = grid.getSelectionModel().getSelection()
			if(records.length != 1){
				globalObject.infoTip('请先需要修改的费用信息！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('lookvalueId', records[0].get('lookvalueId'));
			
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			form.findField('cmd').setValue('edit');
			form.findField('lookvalueName').setReadOnly(true);
			form.findField('lookvalueName').setDisabled(true);
			form.findField('lookvalueCode').setReadOnly(true);
			form.findField('lookvalueCode').setDisabled(true);
			form.loadRecord(gridRecord);
			win.show();
		}
	});
	
});