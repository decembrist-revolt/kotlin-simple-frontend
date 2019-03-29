'use strict';

const webpack = require('webpack');
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {

	entry: {
		main: './web/kotlin-simple-frontend-test.js'
	},

	resolve: {
		// alias: {
		// 	"phaser3-kotlin-wrapper": path.resolve(__dirname, 'static/build/phaser3-kotlin-wrapper.js'),
		// 	"kotlinx-html-js": path.resolve(__dirname, 'static/build/kotlinx-html-js.js'),
		// 	"kotlin-simple-frontend": path.resolve(__dirname, 'static/build/kotlin-simple-frontend.js')
		// },
		modules: ['web', 'node_modules']
	},

	output: {
		path: path.resolve(__dirname, 'static'),
		publicPath: 'static/',
		chunkFilename: '[name].js',
		filename: '[name].js'
	},

	// optimization: {
	// 	splitChunks: {
	// 		chunks: 'all'
	// 	}
	// },

	module: {
		rules: [
			{
				test: /\.js$/,
				include: path.resolve(__dirname, 'web'),
				exclude: [
					/kotlin\.js$/, // Kotlin runtime doesn't have sourcemaps at the moment
                    /kotlinx-html-js\.js$/,
				],
				use: ['source-map-loader'],
				enforce: 'pre',
			}
		]
	},

	devtool: 'source-map',
	plugins: [
		new webpack.DefinePlugin({
			'CANVAS_RENDERER': JSON.stringify(true),
			'WEBGL_RENDERER': JSON.stringify(true)
		}),
		new HtmlWebpackPlugin({
			template: path.join(__dirname, 'index.html'),
		}),
	],

	devServer: {
		contentBase: './static',
		historyApiFallback: {
            index: 'index.html'
        },

	}

};
