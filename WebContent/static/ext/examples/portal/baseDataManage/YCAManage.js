Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('App.baseDataManage.YCAManage.InfoWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '费用小类',
				width : 350,
				height : 280,
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
					}, {
						xtype : 'hiddenfield',
						name : 'lookvalue_id'
					},{
						xtype : 'hiddenfield',
						name : 'id'
					},{
						xtype : 'textfield',
						name : 'code',
						allowBlank : false,
						fieldLabel:'编码<font color="red">*</font>'
					},{
						xtype : 'textfield',
						name : 'name',
						fieldLabel:'名称<font color="red">*</font>',
						allowBlank : false
					},
					new Ext.create('Unit.ComboBox',{
						name : 'unit',
						fieldLabel:'单位<font color="red">*</font>'
					}),{
						xtype : 'numberfield',
						name : 'price',
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
									url : appBaseUri + '/yca/saveOrUpdateValue',
									params : vals,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('ycamanage-ycavaluegrid').getStore().loadPage(1);
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
			App.baseDataManage.YCAManage.InfoWindow.superclass.constructor.call(this, config);
		}
	});

	// 运材安代码维护
	Ext.define('Budget.app.baseDataManage.YCAManage', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
				          Ext.create('Budget.app.baseDataManage.YCAManage.YCATypeGrid'), 
						  Ext.create('Budget.app.baseDataManage.YCAManage.YCAValueGrid')
						  ]
			});
			this.callParent(arguments);
		}
	});
	
	//费用大类
	Ext.define('Budget.app.baseDataManage.YCAManage.YCATypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'ycamanage-ycatypegrid',
		region : 'west',
		width : '18%',
		border : true,
		title:"大类名称",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var ycaTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryTreeList',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : ycaTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						
						me.lookValueId = record.data.id;
						if(record.data.depth == 1){
							Ext.getCmp('ycamanage-ycavaluegrid').getStore().load({
								params : {
									'lookTypeId' : me.lookValueId,
									"param2":"onelevel"
								}
							});
						}
						if(record.data.depth == 2){
							Ext.getCmp('ycamanage-ycavaluegrid').getStore().load({
								params : {
									'lookValueId' : me.lookValueId,
									"param2":""
								}
							});
						}
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.baseDataManage.YCAManage.YCAValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'ycamanage-ycavaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('YCAValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id_X',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'name', 'unit', 'category']
			});
			
			var ycaValueStore = Ext.create('Ext.data.Store', {
				model : 'YCAValueList',
				autoLoad : false,
				remoteSort : false,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryYCAInfo',
					extraParams : {"lookValueId": Ext.getCmp('ycamanage-ycatypegrid').lookValueId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var ycaValueColumns = [ 
			{
					text:'', width:25,xtype:'rownumberer'
			},{
				text : "费用代码",
				dataIndex : 'code',
				width : '14%',
				hidden : false
			},{
				text : "费用名称",
				dataIndex : 'name',
				sortable : false,
				width : '30%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '14%'
			},{
				text : "种类",
				dataIndex : 'category',
				sortable : false,
				width : '14%'
			},{
				text : "looktype_id",
				dataIndex : 'looktype_id',
				hidden : true
			}];
			Ext.apply(this, {
				store : ycaValueStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : ycaValueColumns,
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
					store : ycaValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:true,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			ycaValueStore.on('beforeload',function(){
				if(Ext.getCmp('ycamanage-ycatypegrid').getSelectionModel().getSelection()[0].data.depth == 1){
					Ext.apply(ycaValueStore.proxy.extraParams, {"lookTypeId": Ext.getCmp('ycamanage-ycatypegrid').lookValueId,"param2":"onelevel"});
				}else if(Ext.getCmp('ycamanage-ycatypegrid').getSelectionModel().getSelection()[0].data.depth == 2){
					Ext.apply(ycaValueStore.proxy.extraParams, {"lookValueId": Ext.getCmp('ycamanage-ycatypegrid').lookValueId,"param2":""});
				}
				
			});
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var lookValueId = Ext.getCmp('ycamanage-ycatypegrid').lookValueId;
			if (!lookValueId) {
				globalObject.infoTip('请先选择大类！');
				return;
			};
			var win = new App.baseDataManage.YCAManage.InfoWindow({
				hidden : true
			});
			var form = win.down('form').getForm();
			form.findField('lookvalue_id').setValue(lookValueId);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var lookValueId = Ext.getCmp('ycamanage-ycatypegrid').lookValueId;
			if (!lookValueId) {
				globalObject.infoTip('请先选择费用小类！');
				return;
			};
			
			var grid = Ext.getCmp("ycamanage-ycavaluegrid");
			var records = grid.getSelectionModel().getSelection();
			if(records.length != 1){
				globalObject.infoTip('请先选择需要修改的记录！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('id', records[0].get('id'));
			
			var win = new App.baseDataManage.YCAManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('cmd').setValue('edit');
			form.findField('code').setReadOnly(true);
			form.loadRecord(gridRecord);
			win.show();
		}
	});
});