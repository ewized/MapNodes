const path = require('path')

const { javascript: { JavascriptModulesPlugin } } = require('webpack')


module.exports = {
  mode: 'production',
  devtool: 'source-map',
  // we are neirther node or web or any of the preexisting targets, so apply our own
  target: (compiler) => {
    new JavascriptModulesPlugin().apply(compiler)
  },
  devServer: {
    disableHostCheck: true,
    transportMode: 'ws',
    sockPath: '/',
    inline: true,
    port: 3001,
  },
  // We are not running on the web so we dont need to download files, so changes reflect that
  optimization: {
    minimize: false,
    moduleIds: 'named',
    chunkIds: 'named',
  },
  entry: {
    polyfills: './src/main/js/polyfills/index.js',
    mapnodes: './src/main/js/index.js',
  },
  output: {
    path: path.resolve(__dirname, 'src/generated/js'),
    filename: '[name].bundle.js',
  },
  module: {
    rules: [
      // Transpile the source files so we can use newer syntax
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        use: 'ts-loader',
      },
    ],
  },
}
