Ext.onReady(function() {
	
	
	Ext.define('Cscec.app.systemManage.OrganizationTreeManagement', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : [ Ext.create('Cscec.app.systemManage.OrganizationTree', {
					cButtons : me.cButtons,
					cName : me.cName
				}), Ext.create('Cscec.app.systemManage.UserManagement') ]
			});
			this.callParent(arguments);
		}
	});
	// 树形菜单
	var organizationId = 0;
	Ext.define('Cscec.app.systemManage.OrganizationTree', {
		extend : 'Ext.tree.Panel',
		id : 'organizationId',
		plain : true,
		border : true,
		region : 'west',
		width : '18%',
		autoScroll : true,
		initComponent : function() {
			var me = this;
			organizationId = 0;
			var menutreestore = Ext.create('Ext.data.TreeStore', {
				autoLoad : false,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/sys/organization/getOrganizationList',
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			
			menutreestore.addListener("beforeload", function(store,records){
				var new_params = {
					organizationId:organizationId,
				};
				Ext.apply(store.proxy.extraParams, new_params);
			});
			menutreestore.load();
			
			Ext.apply(this, {
				// title : '菜单权限',
				store : menutreestore,
				rootVisible : false,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-view',
					text : '查询',
					scope : this,
					handler : function() {
						
					}
				},{
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增',
					scope : this,
					handler : function(){
						alert();
					}
				}  ]
			});
			this.callParent(arguments);
		},
		listeners : {
			'itemclick' : function(e, record) {
				if (!record.data.leaf) {
					//organizationId = record.data.id;
					var new_params = {
						organizationId:record.data.id,
					};
					Ext.apply(store.proxy.extraParams, new_params);
				}
			},
			'cellclick' : function (_this, td, cellIndex, record, tr, rowIndex, e, eOpts ){
				if (!record.data.leaf) {
					organizationId = record.data.id;
				}
			}
		}
		
	}); 
	
	Ext.define('Cscec.app.systemManage.UserManagement', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		needQuery:true,
		initComponent : function() {
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'userId',
					type : 'long'
				}, 'personCode', 'name', 'personType', 'cardNumber', 'gender', 'age',
				'nation', {
					name : 'attendWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, {
					name : 'attendWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, {
					name : 'enterWorkTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				}, 'political', 'workYears', 'technicalName', {
					name : 'graduationTime',
					type : 'datetime',
					dateFormat : 'Y-m-d'
				},  'education', 'school', 'major', 'origin', 'enableFlag',
				{
					name : 'lastLoginTime',
					type : 'datetime',
					dateFormat : 'Y-m-d H:i:s'
				}]
			});

			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/sysuser/getSysUser',
				proxyDeleteUrl : appBaseUri + '/sys/sysuser/deleteSysUser',
				extraParams : me.extraParams
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'userId',
				width : '5%'
			}, {
				text : "用户名",
				dataIndex : 'name',
				width : '13%'
			}, {
				text : "人员编号",
				dataIndex : 'personCode',
				width : '10%'
			}, {
				text : "人员类别",
				dataIndex : 'personType',
				width : '14%'
			}, {
				text : "身份证号",
				dataIndex : 'cardNumber',
				width : '15%'
			},{
				text : "性别",
				dataIndex : 'gender',
				width : '15%'
			}, {
				text : "年龄",
				dataIndex : 'age',
				width : '15%'
			}, {
				text : "民族",
				dataIndex : 'nation',
				width : '15%'
			}, {
				text : "参加工作时间",
				dataIndex : 'attendWorkTime',
				width : '15%'
			},  {
				text : "进入本公司时间",
				dataIndex : 'enterWorkTime',
				width : '15%'
			},  {
				text : "政治面貌",
				dataIndex : 'political',
				width : '15%'
			},{
				text : "工龄",
				dataIndex : 'workYears',
				width : '15%'
			}, {
				text : "专业技术职务名称",
				dataIndex : 'technicalName',
				width : '15%'
			},{
				text : "毕业时间",
				dataIndex : 'graduationTime',
				width : '15%'
			},{
				text : "最高学历",
				dataIndex : 'education',
				width : '15%'
			},{
				text : "学校",
				dataIndex : 'school',
				width : '15%'
			},{
				text : "专业(中建)",
				dataIndex : 'major',
				width : '15%'
			},{
				text : "籍贯(中建)",
				dataIndex : 'origin',
				width : '15%'
			},{
				text : "最后登录时间",
				dataIndex : 'lastLoginTime',
				width : '19%'
			}, {
				xtype : 'actioncolumn',
				width : '8%',
				items : [ {
					iconCls : 'icon-view',
					tooltip : '查看',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'View'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.UserManagementWindow({
							hidden : true
						});
						var form = win.down('form').getForm();
						form.loadRecord(gridRecord);
						form.findField('userName').setReadOnly(true);
						form.findField('password').hide();
						form.findField('realName').setReadOnly(true);
						form.findField('tel').setReadOnly(true);
						form.findField('email').setReadOnly(true);
						form.findField('lastLoginTime').show().setReadOnly(true);
						form.findField('roleName').setValue(gridRecord.get('roleName')).setReadOnly(true);
						Ext.getCmp('usermanagementwindow-save').hide();
						Ext.getCmp('usermanagementwindow-cancel').hide();
						Ext.getCmp('usermanagementwindow-accept').show();
						win.show();
					}
				}, {
					iconCls : 'edit',
					tooltip : '修改',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'Edit'),
					handler : function(grid, rowIndex, colIndex) {
						var gridRecord = grid.getStore().getAt(rowIndex);
						var win = new App.UserManagementWindow({
							hidden : true
						});
						win.setHeight(250);
						var form = win.down('form').getForm();
						form.loadRecord(gridRecord);
						form.findField("cmd").setValue("edit");
						form.findField("userName").setReadOnly(true);
						form.findField('password').hide();
						// form.findField('roleName').setRawValue(gridRecord.get('roleName'));
						win.show();
					}
				}, {
					iconCls : 'icon-delete',
					tooltip : '删除',
					disabled : !globalObject.haveActionMenu(me.cButtons, 'Delete'),
					handler : function(grid, rowIndex, colIndex) {
						var entity = grid.getStore().getAt(rowIndex);
						singleId = entity.get('id');
						me.onDeleteClick();
					}
				} ]
			} ];
			
			Ext.apply(this, {
				id : 'usermanagementgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);
			this.callParent(arguments);
		},
		onAddClick : function() {
			new App.UserManagementWindow().show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("usermanagementgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('id');
			var gridRecord = grid.getStore().findRecord('id', id);
			var win = new App.UserManagementWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			form.findField('userName').setReadOnly(true);
			form.findField('password').hide();
			form.findField('realName').setReadOnly(true);
			form.findField('tel').setReadOnly(true);
			form.findField('email').setReadOnly(true);
			form.findField('lastLoginTime').show().setReadOnly(true);
			form.findField('roleName').setValue(grid.getSelectionModel().getSelection()[0].get('roleName')).setReadOnly(true);
			Ext.getCmp('usermanagementwindow-save').hide();
			Ext.getCmp('usermanagementwindow-cancel').hide();
			Ext.getCmp('usermanagementwindow-accept').show();
			win.show();
		},
		onQueryClick:function() {
			var winQuery = new App.UserManagementWindowQuery();
			var form = winQuery.down('form').getForm();
			form.setValues(Ext.getCmp('usermanagementgrid').getStore().proxy.extraParams)
			winQuery.show(); 
		}
	});
});