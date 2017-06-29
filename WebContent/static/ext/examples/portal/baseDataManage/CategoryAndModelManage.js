// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('App.baseDataManage.CategoryAndModelManage.InfoWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '运材安维护',
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
						xtype : 'hiddenfield',
						name : 'unitProject_Id'
					},{
						xtype : 'hiddenfield',
						name : 'id'
					},{
						xtype: 'treecombox', 
						name : 'lookValueId',
						itemId: 'myActionColumn',
						fieldLabel: '编号分类<font color="red">*</font>',
						displayField: 'text', 
					    store: Ext.create('Ext.data.TreeStore', {   
					        fields: ['text','id','parentId'],  
					        root: {  
					            text: '所有分类',  
					            id: -1,  
					            expanded: true  
					        },  
					        proxy: {   
					            type: 'ajax',   
					            url: appBaseUri + '/yca/queryTreeList'
					        }  
					    })
					},{
						xtype : 'textfield',
						name : 'code',
						allowBlank : false,
						fieldLabel:'编号<font color="red">*</font>'
					},{
						xtype : 'textfield',
						name : 'name',
						fieldLabel:'名称<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'textfield',
						name : 'unit',
						fieldLabel:'单位<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'numberfield',
						name : 'content',
						allowDecimals: false,
						fieldLabel:'含量<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'numberfield',
						name : 'amount',
						decimalPrecision: 5,
						fieldLabel:'数量<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'numberfield',
						name : 'price',
						decimalPrecision: 5,
						fieldLabel:'含税单价<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'numberfield',
						name : 'rate',
						decimalPrecision: 5,
						fieldLabel:'税率<font color="red">*</font>',
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
			App.baseDataManage.CategoryAndModelManage.InfoWindow.superclass.constructor.call(this, config);
		}
	});
	
	Ext.define('App.baseDataManage.CategoryAndModelManage.InfoWindowCM', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '子目新增',
				width : 420,
				height : 220,
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
						value : 'editCM'
					},{
						xtype : 'hiddenfield',
						name : 'parentId'
					},{
						xtype : 'hiddenfield',
						name : 'id'
					},{
						xtype : 'textfield',
						name : 'code',
						allowBlank : false,
						fieldLabel:'子目编码<font color="red">*</font>'
					},{
						xtype : 'textfield',
						name : 'name',
						fieldLabel:'子目名称<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'textfield',
						name : 'unit',
						fieldLabel:'计量单位<font color="red">*</font>',
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
			App.baseDataManage.CategoryAndModelManage.InfoWindowCM.superclass.constructor.call(this, config);
		}
	});

	// 费用代码维护
	Ext.define('Budget.app.baseDataManage.CategoryAndModelManage', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
				          Ext.create('Budget.app.baseDataManage.CategoryAndModelManage.CMGrid'), 
				          {	//title: 'West Region is collapsible', 
				              region:'center', 
				              //xtype: 'panel', 
				              //collapsible: true, 
				              id: 'center-region-container', 
				              layout: 'border', 
				              items : [
				                       	  Ext.create('Budget.app.baseDataManage.CategoryAndModelManage.CMDetailGrid'),
										  Ext.create('Budget.app.baseDataManage.CategoryAndModelManage.YCAGrid')
				                       ]
				          }
					    ]
			});
			this.callParent(arguments);
		}
	});
	
	//模块 树状 
	Ext.define('Budget.app.baseDataManage.CategoryAndModelManage.CMGrid', {
		extend : 'Ext.tree.Panel',
		id : 'categoryandmodelmanage-cmgrid',
		region : 'west',
		width : '12%',
		border : true,
		title:"模块名称",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/getCategoryModelList',
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
						if(record.data.id == 0){
							return;
						}
						
						me.parentId = record.data.id;
						
						Ext.getCmp('categoryandmodelmanage-cmdetailgrid').getStore().load({
							params : {
								'parentId' : me.parentId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.baseDataManage.CategoryAndModelManage.CMDetailGrid', {
		extend : 'Ext.grid.Panel',
		id : 'categoryandmodelmanage-cmdetailgrid',
		plain : true,
		region : 'center',
		border:true,
		initComponent : function() {
			var me = this;
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'price','transportFee','materialFee','installationFee',]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/queryDetailCategoryModel',
					extraParams : {"parentId": Ext.getCmp('categoryandmodelmanage-cmgrid').parentId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "子目编码",
				dataIndex : 'code',
				width : '12%',
				hidden : false
			},{
				text : "子目名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%'
			}, {
				text : "计量单位",
				dataIndex : 'unit',
				sortable : false,
				width : '10%'
			},{
				text : "单价",
				dataIndex : 'price',
				sortable : false,
				width : '10%'
			},{
				text : "运费",
				dataIndex : 'transportFee',
				sortable : false,
				width : '10%'
			},{
				text : "材料费",
				dataIndex : 'materialFee',
				sortable : false,
				width : '10%'
			},{
				text : "安装费",
				dataIndex : 'installationFee',
				sortable : false,
				width : '10%'
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			}];
			Ext.apply(this, {
				store : costValueStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						
						me.unitProject_Id = record.data.id;
						
						Ext.getCmp('categoryandmodelmanage-YCAGrid').getStore().load({
							params : {
								'unitProject_Id' : me.unitProject_Id
							}
						});
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增',
					scope : this,
					handler : me.newCodeCodeFun
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
			
			costValueStore.on('beforeload',function(){
				Ext.apply(costValueStore.proxy.extraParams, {"parentId": Ext.getCmp('categoryandmodelmanage-cmgrid').parentId});
			});
			
			
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var parentId = Ext.getCmp('categoryandmodelmanage-cmgrid').parentId;
			if (!parentId) {
				globalObject.infoTip('请先选模块名称！');
				return;
			};
			var win = new App.baseDataManage.CategoryAndModelManage.InfoWindowCM({
				hidden : true
			});
			var form = win.down('form').getForm();
			form.findField('parentId').setValue(parentId);
			win.show();
		}
	});
	
	
	//运材安明细
	Ext.define('Budget.app.baseDataManage.CategoryAndModelManage.YCAGrid', {
		extend : 'Ext.grid.Panel',
		id : 'categoryandmodelmanage-YCAGrid',
		plain : true,
		region : 'south',
		height : '50%',
		border:true,
		initComponent : function() {
			var me = this;
			Ext.define('YCAGridList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'content','amount','noPrice','price','rate','sumNoPrice','sumPrice','lookValueId']
			});
			
			var YCAGridStore = Ext.create('Ext.data.Store', {
				model : 'YCAGridList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/categorymodel/queryDetailYCA',
					extraParams : {"unitProject_Id": Ext.getCmp('categoryandmodelmanage-cmdetailgrid').unitProject_Id},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "编号",
				dataIndex : 'code',
				width : '12%',
				hidden : false
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '7%'
			},{
				text : "含量",
				dataIndex : 'content',
				sortable : false,
				width : '7%'
			},{
				text : "数量",
				dataIndex : 'amount',
				sortable : false,
				width : '7%'
			},{
				text : "不含税单价",
				dataIndex : 'noPrice',
				sortable : false,
				width : '7%'
			},{
				text : "含税单价",
				dataIndex : 'price',
				sortable : false,
				width : '7%'
			},{
				text : "税率",
				dataIndex : 'rate',
				sortable : false,
				width : '7%'
			},{
				text : "不含税合价",
				dataIndex : 'sumNoPrice',
				sortable : false,
				width : '10%'
			},{
				text : "含税合价",
				dataIndex : 'sumPrice',
				sortable : false,
				width : '10%'
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "lookValueId",
				dataIndex : 'lookValueId',
				hidden : true
			}];
			Ext.apply(this, {
				store : YCAGridStore,
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增',
					scope : this,
					handler : me.newCodeCodeFun
				},{
					xtype : 'button',
					iconCls : 'icon-edit',
					text : '修改',
					scope : this,
					handler : me.editCodeCodeFun
				}],
				bbar : Ext.create('Ext.PagingToolbar', {
					store : YCAGridStore,
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
		newCodeCodeFun: function(){
			var me = this;
			var unitProject_Id = Ext.getCmp('categoryandmodelmanage-cmdetailgrid').unitProject_Id;
			if (!unitProject_Id) {
				globalObject.infoTip('请先选择一条子目记录！');
				return;
			};
			var win = new App.baseDataManage.CategoryAndModelManage.InfoWindow({
				hidden : true
			});
			var form = win.down('form').getForm();
			form.findField('unitProject_Id').setValue(unitProject_Id);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var unitProject_Id = Ext.getCmp('categoryandmodelmanage-cmdetailgrid').unitProject_Id;
			if (!unitProject_Id) {
				globalObject.infoTip('请先选择一条子目记录！');
				return;
			};
			
			var grid = Ext.getCmp("categoryandmodelmanage-YCAGrid");
			var records = grid.getSelectionModel().getSelection();
			if(records.length != 1){
				globalObject.infoTip('请先选择需要修改的记录！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('id', records[0].get('id'));
			
			var win = new App.baseDataManage.CategoryAndModelManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('unitProject_Id').setValue(unitProject_Id);
			form.findField('cmd').setValue('edit');
			form.findField('code').setReadOnly(true);
			form.findField('name').setReadOnly(true);
			form.findField('unit').setReadOnly(true);
			form.findField('lookValueId').setValue(gridRecord.data.lookValueId);
			win.down('#myActionColumn').hide();
			
			form.loadRecord(gridRecord);
			win.show();
		
		}
	});
});