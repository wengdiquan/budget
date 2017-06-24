// 新建项目
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('App.bussinessProcess.ProjectInfo.Project', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				width : 350,
				height : 230,
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
						name : 'parentId'
					},{
						xtype : 'hiddenfield',
						name : 'isLeaf'
					},{
						xtype : 'hiddenfield',
						name : 'level'  
					},{
						xtype : 'hiddenfield',
						name : 'projectId'
					},{
						xtype : 'textfield',
						name : 'projectName',
						fieldLabel:'名称<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'textarea',
						name : 'remark',
						fieldLabel:'备注'
					},{xtype:'radiogroup', fieldLabel:'是否有效',   
		                columnWidth:0.7,items: [  
		                    { boxLabel: '是', name: 'enableFlag', inputValue: '1' , checked:true},   
		                    { boxLabel: '否', name: 'enableFlag', inputValue: '0' }
		                  
		            ]}],
					buttons : [ '->', {
						id : 'project-save',
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
									url : appBaseUri + '/project/saveOrUpdateProject',
									params : vals,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												var store = Ext.getCmp('projectinfo-projecttreeid').getStore();
												//如果是项目的话
												if(window.eventSource == "PJ"){
												}
												
												//如果是单项工程的话
												if(window.eventSource == "SPJ"){
													var newnode= [{id: res.data, text:vals['projectName'],
														leaf:false, iconCls:'icon-room'}];
													
													Ext.getCmp('projectinfo-projecttreeid').sproject = res.data;
												}
												
												//如果是单位工程的话
												if(window.eventSource == "BPJ"){
													var newnode= [{id: res.data, text:vals['projectName'],
														leaf:true, iconCls:'icon-wood'}];
													Ext.getCmp('projectinfo-projecttreeid').bproject = res.data;
												}
												
												var pnode = store.getNodeById(vals['parentId']); //查找父节点
						                        pnode.appendChild(newnode); //添加子节点  
						                        pnode.expand(); 
						                        Ext.getCmp('projectinfo-projecttreeid').getSelectionModel().select(store.getNodeById(res.data));  
				
												
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
						id : 'project-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					},'->']
				} ]
			});
			App.bussinessProcess.ProjectInfo.Project.superclass.constructor.call(this, config);
		}
	});
	
	//新建项目
	Ext.define('Budget.app.bussinessProcess.ProjectInfo', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : [ Ext.create('Budget.app.bussinessProcess.ProjectInfo.projectTree'), 
						  Ext.create('Budget.app.bussinessProcess.ProjectInfo.projectSummeryGrid') ]
			});
			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectInfo.projectTree', {
		extend : 'Ext.tree.Panel',
		id : 'projectinfo-projecttreeid',
		plain : true,
		border : true,
		region : 'west',
		width : '20%',
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			
			var store = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy:{
					type : 'ajax',
					url : appBaseUri + '/project/getProjectTreeList',
					extraParams:{"source":"init"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			
			Ext.apply(this, {
				// title : '菜单权限',
				rootVisible : false,
				store:store,
				tbar:[{
						width : 80,
						xtype : 'button',
						text : '新增',
						iconCls : 'icon-add',
						menu : [/*{
							text : '新增项目',
							handler : me.addProjectFun,
							iconCls : 'icon-buliding'
						}, '-', */{
							text : '新建单项工程',
							handler : me.addSingleProjectFun,
							iconCls : 'icon-room'
						}, '-', {
							text : '新建单位工程',
							handler : me.addBitProjectFun,
							iconCls : 'icon-wood'
						} ]
					},
					{
						width : 80,
						xtype : 'button',
						text : '移除',
						iconCls : 'icon-delete',
						handler:function(){
							
							 var records = Ext.getCmp('projectinfo-projecttreeid').getSelectionModel().getSelection()
							 if(records.length != 1){
								 globalObject.infoTip('请选择一项进行移除');
								 return;
							 }
							 
							 if(records[0].data.depth == 1){
								 globalObject.infoTip('不能移除项目');
								 return;
							 }
							 
							 globalObject.confirmTip("删除该节点会同时删除已经填写的单位工程，确认删除吗?", 
								 function(btn){
									 if("yes" == btn){
										 Ext.getCmp('projectinfo-projecttreeid').getEl().mask('数据处理中，请稍候...');
										 Ext.Ajax.request({
											 url : appBaseUri + '/project/saveOrUpdateProject',
											 params : {
												 "cmd":"edit",
												 "projectId":records[0].data.id
											 },
											 method : "POST",
											 success : function(response) {
												 if (response.responseText != '') {
													 var res = Ext.JSON.decode(response.responseText);
													 if (res.success) {
														 records[0].remove();
													 } else {
														 globalObject.errTip(res.msg);
													 }
												 }
											 },
											 failure : function(response) {
												 globalObject.errTip('操作失败！');
											 }
										 });
										 
										 Ext.getCmp('projectinfo-projecttreeid').getEl().unmask();
									 }
							 	});
						}
					}
				]
			});
			this.callParent(arguments);
		},
		addProjectFun:function(){
			if(Ext.getCmp('projectinfo-projecttreeid').projectId != null){
				globalObject.infoTip('项目不可以重建！');
				return;
			}
			
			var store = Ext.getCmp('projectinfo-projecttreeid').getStore();
			
			store.addListener("beforeload", function(store,records){
				var new_params = {
				};
				Ext.apply(store.proxy.extraParams, new_params);
			});
			
			var win = new App.bussinessProcess.ProjectInfo.Project({title:"新建项目", hidden:true, eventSource:'PJ'});
			var form = win.down('form').getForm();
			form.findField('level').setValue("1");
			form.findField('isLeaf').setValue("0");
			
			win.show();
		},
		addSingleProjectFun:function(){
			
			if(Ext.getCmp('projectinfo-projecttreeid').project == null){
				globalObject.infoTip('请先选择项目');
				return;
			}
			var win = new App.bussinessProcess.ProjectInfo.Project({title:"新建单项工程", hidden:true, eventSource:'SPJ'});
			var form = win.down('form').getForm();
			form.findField('level').setValue("2");
			form.findField('isLeaf').setValue("0");
			form.findField('parentId').setValue(Ext.getCmp('projectinfo-projecttreeid').project);
			win.show();
		},
		addBitProjectFun:function(){
			
			if(Ext.getCmp('projectinfo-projecttreeid').sproject == null){
				globalObject.infoTip('请选择单项工程');
				return;
			}
			
			var win = new App.bussinessProcess.ProjectInfo.Project({title:"新建单位工程", hidden:true, eventSource:'BPJ'});
			var form = win.down('form').getForm();
			form.findField('level').setValue("3");
			form.findField('isLeaf').setValue("1");
			form.findField('parentId').setValue(Ext.getCmp('projectinfo-projecttreeid').sproject);
			win.show();
		},
		listeners : {
			'itemclick' : function(_this, record, item, index, e, eOpts) {
				var store = Ext.getCmp('projectinfo-projecttreeid').getStore();
				Ext.apply(store.proxy.extraParams, {"source":"click"});
				
				var costValueStore = Ext.getCmp("projectinfo-projectsummerygrid").getStore();
				
				Ext.apply(costValueStore.proxy.extraParams, {"projectId":record.data.id});

				var costValueStore = Ext.getCmp("projectinfo-projectsummerygrid").getStore();
				
				if(record.data.depth == 1){
					Ext.apply(costValueStore.proxy.extraParams, {"pro":"1"});
					Ext.getCmp('projectinfo-projecttreeid').project = record.data.id;
					Ext.apply(store.proxy.extraParams, {"source": "pj"});
					costValueStore.reload();
				}
				
				if(record.data.depth == 2){
					Ext.apply(costValueStore.proxy.extraParams, {"pro":"2"});
					Ext.getCmp('projectinfo-projecttreeid').sproject = record.data.id;
					Ext.apply(store.proxy.extraParams, {"source": "spj"});
					costValueStore.reload();
				}
				
				if(record.data.depth == 3){
					costValueStore.removeAll();
				}
			},
			'itemdblclick' : function(_this, record, td, cellIndex, tr, rowIndex, e, eOpts){
				var store = Ext.getCmp('projectinfo-projecttreeid').getStore();
				if(record.data.leaf){
					var pnode = store.getNodeById(record.data.parentId);
					var rnode = store.getNodeById(pnode.data.parentId);
					
					var bitWin = Ext.create("Budget.app.bussinessProcess.ProjectBitWin",{
						title : '单位工程:' + rnode.data.text + "->" + pnode.data.text + "->" + record.data.text,
						bitProjectId: record.data.id
					});
					
					bitWin.show();
				}
			}
		}
	});
	// 项目汇总
	Ext.define('Budget.app.bussinessProcess.ProjectInfo.projectSummeryGrid', {
		extend : 'Ext.grid.Panel',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'project_id',
				fields : [ 'index', {
					name : 'project_id',
					type : 'int'
				}, 'project_name', 'totalAmount', 'projectPercent', 'transportFee','materialFee','installationFee','csFee' ]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/project/getSumList',
					extraParams : {"xx":""},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			Ext.apply(this, {
				id : 'projectinfo-projectsummerygrid',
				store : costValueStore,
				title:"汇总信息",
				/*features: [{
		                ftype: 'summary'
		            }],*/
				selModel : Ext.create('Ext.selection.Model'),
				columns : [{
						text:'序号', width:40,dataIndex:'index'
					},
					{
						text:'名称', width:120, 	dataIndex : 'project_name',
					},
					{
						text:'项目造价(元)', width:120, 	dataIndex : 'totalAmount', align:"right"
					},{
						text:'占造价百分比(%)', width:120, 	dataIndex : 'projectPercent', align:"right"
					},
					{
						text:"其中",
			            columns:[
			                {text: "运输费", width: 80, dataIndex: 'transportFee', align:"right", sortable: false},
			                {text: "材料费", width: 80, dataIndex: 'materialFee', align:"right", sortable: false},
			                {text: "安装费", width: 80, dataIndex: 'installationFee', align:"right", sortable: false},
			                {text: "措施费", width: 80, dataIndex: 'csFee', align:"right", sortable: false}]
					}
				],
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
		}
	});
});