/*单位工程子目选择*/
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectItemWin', {
		extend : 'Ext.window.Window',
		title:"查询",
		bitItemGrid:"",
		initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				width : "70%",
				height : "70%",
				bodyPadding : '5 5',
				modal : true,
				layout : 'fit',
				items : [Ext.create("Budget.app.bussinessProcess.ProjectBitWin.TabPanel", {
						bitProjectId: me.bitProjectId,
						bitItemGrid: me.bitItemGrid
					})
				]
  			});
  			this.callParent(arguments);
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin.TabPanel', {
		extend : 'Ext.tab.Panel',
        initComponent : function() {
          	var me = this;
  			Ext.apply(this, {
  				layout:'fit',
  				items:[{  
                    title: '定额',  
                    layout : 'border',
                    items:[
                    	 Ext.create('Budget.app.bussinessProcess.ProjectBitWin.TabPanel.CMGrid',{
                    		 bitItemGrid:me.bitItemGrid
                    	 }),
                    	 Ext.create('Budget.app.bussinessProcess.ProjectBitWin.TabPanel.CMDetailGrid', {
                    		 bitItemGrid:me.bitItemGrid
                    	 })
                    ],
                    itemId: 'dinge'  
                },{  
                    title: '运材安',  
                    items:[
                    	Ext.create("Budget.app.bussinessProcess.ProjectYCATotalPanel", {
                    		
                    	})
                    ],
                    itemId: 'yca'
                }]
  			});
        	
			this.callParent(arguments);
		}
	});
	
	//模块名称tree
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin.TabPanel.CMGrid', {
		extend : 'Ext.tree.Panel',
		id:"projectbitwin-tabpanel-cmgrid",
		region : 'west',
		width : '35%',
		border : true,
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
						
						Ext.getCmp('projectbitwin-tabpanel-cmdetailgrid').getStore().load({
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
	
	Ext.define('Budget.app.bussinessProcess.ProjectBitWin.TabPanel.CMDetailGrid', {
		extend : 'Ext.grid.Panel',
		plain : true,
		region : 'center',
		id:'projectbitwin-tabpanel-cmdetailgrid',
		border:true,
		initComponent : function() {
			var me = this;
			
			var bitItemGrid = me.bitItemGrid;
			
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
					extraParams : {"parentId": Ext.getCmp('projectbitwin-tabpanel-cmgrid').parentId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [{
				text:'', width:25,xtype:'rownumberer'
			},{
				text : "编码",
				dataIndex : 'code',
				width : '15%',
				hidden : false
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '40%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '20%'
			},{
				text : "单价",
				dataIndex : 'price',
				sortable : false,
				width : '20%'
			},{
				text : "运费",
				dataIndex : 'transportFee',
				sortable : false,
				width : '10%',
				hidden : true
			},{
				text : "材料费",
				dataIndex : 'materialFee',
				sortable : false,
				hidden : true,
				width : '10%'
			},{
				text : "安装费",
				dataIndex : 'installationFee',
				sortable : false,
				hidden : true,
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
						
						var bitRecord =  bitItemGrid.getSelectionModel().getSelection()[0];
						
						//修改
						if(bitRecord.get("code") == null || bitRecord.get("code")  == ""){
								
							bitRecord.set('code', record.get('code'));
							bitRecord.set('type', '定');
							bitRecord.set('name', record.get('name'));
							bitRecord.set('unit', record.get('unit'));
							bitRecord.set('amount', null);
							bitRecord.set('dtgcl', 1);
							bitRecord.set('singlePrice', 1);
							bitRecord.set('price', 1);
							bitRecord.set('sumPrice', 1);
							bitRecord.set('remark', null);
						}else{
							//数据对象
							var newRecord = {
									code: record.get('code'),
									type: '定',
									name: record.get('name'),
									unit: record.get('unit'),
									amount:null,
									dtgcl:1,
									singlePrice:1,
									price:1,
									sumPrice:1,
									remark:null
							};
							bitItemGrid.getStore().add(newRecord);
						}
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
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